import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BookDAO {

   
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, genre, total_qty, available_qty) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getTotalQty());
            pstmt.setInt(6, book.getAvailableQty());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Returns true if insertion was successful
            
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        String sql = "SELECT * FROM books";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("genre"),
                    rs.getInt("total_qty"),
                    rs.getInt("available_qty")
                );
                bookList.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving books: " + e.getMessage());
        }
        return bookList;
    }

    
    public Book getBookByTitle(String title) {
        String sql = "SELECT * FROM books WHERE title = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("genre"),
                    rs.getInt("total_qty"),
                    rs.getInt("available_qty")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Not found
    }

    
    // This method updates TWO tables: decrements book stock AND creates an issue record.
    public boolean issueBook(int memberId, int bookId) {
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateBookStmt = null;
        PreparedStatement issueRecordStmt = null;

        String checkStockSql = "SELECT available_qty FROM books WHERE book_id = ?";
        String updateStockSql = "UPDATE books SET available_qty = available_qty - 1 WHERE book_id = ?";
        String insertIssueSql = "INSERT INTO issue_records (member_id, book_id, issue_date, due_date, status) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'ISSUED')";

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // ⚠️ START TRANSACTION

            // Step 1: Check availability
            checkStmt = conn.prepareStatement(checkStockSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                int available = rs.getInt("available_qty");
                if (available <= 0) {
                    System.out.println("Book is out of stock!");
                    return false; 
                }
            } else {
                System.out.println("Book not found!");
                return false;
            }

            // Step 2: Decrement Stock
            updateBookStmt = conn.prepareStatement(updateStockSql);
            updateBookStmt.setInt(1, bookId);
            updateBookStmt.executeUpdate();

            // Step 3: Create Issue Record
            issueRecordStmt = conn.prepareStatement(insertIssueSql);
            issueRecordStmt.setInt(1, memberId);
            issueRecordStmt.setInt(2, bookId);
            issueRecordStmt.executeUpdate();

            conn.commit(); // ✅ COMMIT TRANSACTION
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // ❌ ROLLBACK ON ERROR
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (checkStmt != null) checkStmt.close();
                if (updateBookStmt != null) updateBookStmt.close();
                if (issueRecordStmt != null) issueRecordStmt.close();
                if (conn != null) conn.setAutoCommit(true); // Reset to default
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // 5. RETURN BOOK
    // Updates issue_record status and increments book stock.
    public boolean returnBook(int memberId, int bookId) {
        Connection conn = null;
        PreparedStatement findIssueStmt = null;
        PreparedStatement updateIssueStmt = null;
        PreparedStatement updateBookStmt = null;
        
        // Find the active issue record
        String findIssueSql = "SELECT issue_id, due_date FROM issue_records WHERE member_id = ? AND book_id = ? AND status = 'ISSUED'";
        String updateIssueSql = "UPDATE issue_records SET return_date = CURDATE(), status = 'RETURNED', fine_amount = ? WHERE issue_id = ?";
        String updateStockSql = "UPDATE books SET available_qty = available_qty + 1 WHERE book_id = ?";

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // ⚠️ START TRANSACTION

            // Step 1: Find the issue record
            findIssueStmt = conn.prepareStatement(findIssueSql);
            findIssueStmt.setInt(1, memberId);
            findIssueStmt.setInt(2, bookId);
            ResultSet rs = findIssueStmt.executeQuery();

            int issueId = -1;
            Date dueDate = null;

            if (rs.next()) {
                issueId = rs.getInt("issue_id");
                dueDate = rs.getDate("due_date");
            } else {
                System.out.println("No active issue record found for this Member and Book.");
                return false;
            }

            // Step 2: Calculate Fine (Simple logic: 10 Rs per day late)
            long millis = System.currentTimeMillis();
            Date returnDate = new Date(millis);
            double fine = 0.0;
            
            if (returnDate.after(dueDate)) {
                long diffInMillies = Math.abs(returnDate.getTime() - dueDate.getTime());
                long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
                fine = diffInDays * 10.0; // 10 Rs per day
            }

            // Step 3: Update Issue Record
            updateIssueStmt = conn.prepareStatement(updateIssueSql);
            updateIssueStmt.setDouble(1, fine);
            updateIssueStmt.setInt(2, issueId);
            updateIssueStmt.executeUpdate();

            // Step 4: Increment Stock
            updateBookStmt = conn.prepareStatement(updateStockSql);
            updateBookStmt.setInt(1, bookId);
            updateBookStmt.executeUpdate();

            conn.commit(); // ✅ COMMIT TRANSACTION
            System.out.println("Book Returned! Fine Amount: " + fine);
            return true;

        } catch (SQLException e) {
             if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // ❌ ROLLBACK
            }
            e.printStackTrace();
            return false;
        } finally {
            // Close resources...
             try {
                if (findIssueStmt != null) findIssueStmt.close();
                if (updateIssueStmt != null) updateIssueStmt.close();
                if (updateBookStmt != null) updateBookStmt.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
