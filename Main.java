import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BookDAO bookDAO = new BookDAO();
    private static Member currentUser = null; // Stores the logged-in user

    public static void main(String[] args) {
        // ---------------------------------------------------------
        // 1. SYLLABUS REQUIREMENT: Multithreading
        // Start a background thread that checks for overdue books
        // while the user interacts with the console.
        // ---------------------------------------------------------
        OverdueCheckTask backgroundTask = new OverdueCheckTask();
        backgroundTask.setDaemon(true); // Daemon thread ends when main program ends
        backgroundTask.start();

        System.out.println("=============================================");
        System.out.println("      DIGITAL LIBRARY MANAGEMENT SYSTEM      ");
        System.out.println("=============================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Enter Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    System.out.println("Exiting System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    // ---------------------------------------------------------
    // AUTHENTICATION LOGIC
    // ---------------------------------------------------------
    private static void login() {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        // Simple JDBC Authentication
        String sql = "SELECT * FROM members WHERE email = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentUser = new Member(
                    rs.getInt("member_id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("phone_number")
                );
                
                System.out.println("\n✅ Login Successful! Welcome, " + currentUser.getFullName());
                
                if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                    adminMenu();
                } else {
                    studentMenu();
                }
            } else {
                System.out.println("❌ Login Failed! Invalid Credentials.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // ADMIN MENU (Librarian)
    // ---------------------------------------------------------
    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. Add New Book");
            System.out.println("2. View All Books");
            System.out.println("3. View Issued Books Report"); // File I/O placeholder
            System.out.println("4. Logout");
            System.out.print("Enter Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    // Input Book Details
                    System.out.print("Enter Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter Author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter ISBN: ");
                    String isbn = scanner.nextLine();
                    System.out.print("Enter Genre: ");
                    String genre = scanner.nextLine();
                    System.out.print("Enter Total Qty: ");
                    int qty = getIntInput();
                    
                    Book newBook = new Book(title, author, isbn, genre, qty, qty);
                    if (bookDAO.addBook(newBook)) {
                        System.out.println("✅ Book Added Successfully!");
                    } else {
                        System.out.println("❌ Failed to Add Book.");
                    }
                    break;
                case 2:
                    viewAllBooks();
                    break;
                case 3:
                    System.out.println("⚠️ Feature: This would generate a .txt file of issued books.");
                    // You can implement File I/O here to save report to "report.txt"
                    break;
                case 4:
                    currentUser = null;
                    return; // Go back to Main Menu
                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    // ---------------------------------------------------------
    // STUDENT MENU
    // ---------------------------------------------------------
    private static void studentMenu() {
        while (true) {
            System.out.println("\n--- STUDENT DASHBOARD ---");
            System.out.println("1. View Available Books");
            System.out.println("2. Issue a Book");
            System.out.println("3. Return a Book");
            System.out.println("4. Logout");
            System.out.print("Enter Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    viewAllBooks();
                    break;
                case 2:
                    System.out.print("Enter Book ID to Issue: ");
                    int issueId = getIntInput();
                    if (bookDAO.issueBook(currentUser.getMemberId(), issueId)) {
                        System.out.println("✅ Book Issued Successfully! Due in 7 days.");
                    } else {
                        System.out.println("❌ Failed to Issue Book (Out of stock or Invalid ID).");
                    }
                    break;
                case 3:
                    System.out.print("Enter Book ID to Return: ");
                    int returnId = getIntInput();
                    if (bookDAO.returnBook(currentUser.getMemberId(), returnId)) {
                        System.out.println("✅ Book Returned Successfully.");
                    } else {
                        System.out.println("❌ Return Failed. Check if you actually borrowed this book.");
                    }
                    break;
                case 4:
                    currentUser = null;
                    return;
                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    // Helper to View Books
    private static void viewAllBooks() {
        List<Book> books = bookDAO.getAllBooks();
        System.out.println("\n--- LIBRARY COLLECTION ---");
        System.out.printf("%-5s %-30s %-20s %-10s%n", "ID", "Title", "Author", "Available");
        System.out.println("--------------------------------------------------------------------");
        for (Book b : books) {
            System.out.printf("%-5d %-30s %-20s %-10d%n", 
                b.getBookId(), 
                b.getTitle().length() > 28 ? b.getTitle().substring(0, 27) + "..." : b.getTitle(), 
                b.getAuthor(), 
                b.getAvailableQty());
        }
    }

    // Helper for Integer Input
    private static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

/**
 * THREAD CLASS
 * Checks for overdue books in the background.
 */
class OverdueCheckTask extends Thread {
    @Override
    public void run() {
        try {
            // Run only once or in a loop with sleep
            // Here we simulate a check that happens 2 seconds after startup
            Thread.sleep(2000); 
            
            System.out.println("\n[SYSTEM NOTIFICATION] Checking for overdue books...");
            
            String sql = "SELECT COUNT(*) AS overdue_count FROM issue_records WHERE due_date < CURDATE() AND status = 'ISSUED'";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                 
                if (rs.next()) {
                    int count = rs.getInt("overdue_count");
                    if (count > 0) {
                        System.out.println("[ALERT] There are " + count + " overdue books in the system!");
                    } else {
                        System.out.println("[SYSTEM] No overdue books found.");
                    }
                }
            } catch (SQLException e) {
                // Silent fail in background thread to not disturb user
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
