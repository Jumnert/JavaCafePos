import java.sql.*;

/**
 * Database connection utility class
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/cafepos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String[] PASSES = {"mony123", "nith2020"}; // two passwords to try

    /**
     * Get database connection. Tries multiple passwords automatically.
     * @return Connection object
     * @throws SQLException if all passwords fail
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found!", e);
        }

        SQLException lastException = null;

        for (String pass : PASSES) {
            try {
                System.out.println("Trying password: " + pass);
                return DriverManager.getConnection(URL, USER, pass);
            } catch (SQLException e) {
                lastException = e; // store the exception and try next password
            }
        }

        throw new SQLException("Could not connect with any provided password.", lastException);
    }

    /**
     * Close resources safely
     */
    public static void closeResources(Connection conn, PreparedStatement pst, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
