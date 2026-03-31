import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

    
    private static final String URL = "jdbc:mysql://localhost:3306/library_db"; 
    
    
    private static final String USER = "root"; 
    private static final String PASSWORD = "password"; 

        public static Connection getConnection() {
        Connection conn = null;
        try {
                       Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Establish the connection using DriverManager
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: MySQL JDBC Driver not found!");
            System.err.println("Make sure you have added the 'mysql-connector-j-x.x.x.jar' to your project build path.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error: Connection to database failed!");
            System.err.println("Check if your MySQL server is running and credentials are correct.");
            e.printStackTrace();
        }
        return conn;
    }

       public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        
        Connection testConn = DBConnection.getConnection();
        
        if (testConn != null) {
            System.out.println("✅ SUCCESS: Connected to 'library_db' successfully!");
        } else {
            System.out.println("❌ FAILURE: Could not connect to the database.");
        }
    }
}
