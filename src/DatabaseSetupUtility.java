import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Utility class for setting up and managing cafe database users
 */
public class DatabaseSetupUtility {
    
    /**
     * Hash a password using SHA-256
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * UTILITY 1: Create default admin account
     * Run this once to set up your initial admin account
     */
    public static void createDefaultAdmin() {
        String defaultUsername = "admin";
        String defaultPassword = "admin123";
        String defaultEmail = "admin@forestcafe.com";
        String defaultFullName = "System Administrator";
        
        try (Connection conn = CafePos.DBConnection.getConnection()) {
            // Check if admin already exists
            String checkQuery = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkPst = conn.prepareStatement(checkQuery);
            checkPst.setString(1, defaultUsername);
            ResultSet rs = checkPst.executeQuery();
            
            if (rs.next()) {
                System.out.println("[X] Admin account already exists!");
                return;
            }
            
            // Create admin account
            String insertQuery = "INSERT INTO users (full_name, username, email, password_hash, role, is_active) VALUES (?, ?, ?, ?, 'admin', 1)";
            PreparedStatement insertPst = conn.prepareStatement(insertQuery);
            insertPst.setString(1, defaultFullName);
            insertPst.setString(2, defaultUsername);
            insertPst.setString(3, defaultEmail);
            insertPst.setString(4, hashPassword(defaultPassword));
            
            int result = insertPst.executeUpdate();
            
            if (result > 0) {
                System.out.println("[OK] Default admin account created successfully!");
                System.out.println("========================================");
                System.out.println("ADMIN CREDENTIALS:");
                System.out.println("   Username: " + defaultUsername);
                System.out.println("   Password: " + defaultPassword);
                System.out.println("========================================");
                System.out.println("WARNING: Change this password immediately after first login!");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error creating admin account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * UTILITY 2: Generate password hash
     * Use this to generate hashes for manual database insertion
     */
    public static void generatePasswordHash() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n========================================");
        System.out.println("PASSWORD HASH GENERATOR");
        System.out.println("========================================");
        
        System.out.print("Enter password to hash: ");
        String password = scanner.nextLine();
        
        String hash = hashPassword(password);
        
        System.out.println("\n[OK] Password Hash Generated:");
        System.out.println("========================================");
        System.out.println(hash);
        System.out.println("========================================");
        System.out.println("\nYou can use this hash in SQL INSERT statements:");
        System.out.println("INSERT INTO users (username, password_hash, role) VALUES ('username', '" + hash + "', 'admin');");
    }

    /**
     * UTILITY 3: List all manager/admin accounts
     */
    public static void listManagerAccounts() {
        try (Connection conn = CafePos.DBConnection.getConnection()) {
            String query = "SELECT user_id, username, full_name, email, role, is_active FROM users WHERE role IN ('admin', 'manager') ORDER BY role, username";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            System.out.println("\n====================================================");
            System.out.println("MANAGER & ADMIN ACCOUNTS");
            System.out.println("====================================================");
            
            boolean found = false;
            while (rs.next()) {
                found = true;
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");
                
                String status = isActive ? "[Active]" : "[Inactive]";
                String roleIcon = role.equals("admin") ? "[ADMIN]" : "[MANAGER]";
                
                System.out.println(String.format("\n%s ID: %d | Role: %s", roleIcon, userId, role.toUpperCase()));
                System.out.println("   Username: " + username);
                System.out.println("   Full Name: " + fullName);
                System.out.println("   Email: " + email);
                System.out.println("   Status: " + status);
                System.out.println("   ----------------------------------------");
            }
            
            if (!found) {
                System.out.println("\n[!] No manager or admin accounts found!");
                System.out.println("   Run createDefaultAdmin() to create one.");
            }
            
            System.out.println("====================================================\n");
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Error listing accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * UTILITY 4: Create manager account 
     */
    public static void createManagerAccount() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n========================================");
        System.out.println("CREATE NEW MANAGER ACCOUNT");
        System.out.println("========================================");
        
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        System.out.print("Role (admin/manager): ");
        String role = scanner.nextLine().toLowerCase();
        
        if (!role.equals("admin") && !role.equals("manager")) {
            System.out.println("[ERROR] Invalid role! Must be 'admin' or 'manager'");
            return;
        }
        
        try (Connection conn = CafePos.DBConnection.getConnection()) {
            // Check if username exists
            String checkQuery = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkPst = conn.prepareStatement(checkQuery);
            checkPst.setString(1, username);
            ResultSet rs = checkPst.executeQuery();
            
            if (rs.next()) {
                System.out.println("[ERROR] Username already exists!");
                return;
            }
            
            // Insert new account
            String insertQuery = "INSERT INTO users (full_name, username, email, password_hash, role, is_active) VALUES (?, ?, ?, ?, ?, 1)";
            PreparedStatement insertPst = conn.prepareStatement(insertQuery);
            insertPst.setString(1, fullName);
            insertPst.setString(2, username);
            insertPst.setString(3, email);
            insertPst.setString(4, hashPassword(password));
            insertPst.setString(5, role);
            
            int result = insertPst.executeUpdate();
            
            if (result > 0) {
                System.out.println("\n[OK] Account created successfully!");
                System.out.println("========================================");
                System.out.println("Username: " + username);
                System.out.println("Role: " + role);
                System.out.println("========================================");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error creating account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * UTILITY 5: Reset a user's password
     */
    public static void resetPassword() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n========================================");
        System.out.println("RESET USER PASSWORD");
        System.out.println("========================================");
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        
        try (Connection conn = CafePos.DBConnection.getConnection()) {
            String updateQuery = "UPDATE users SET password_hash = ? WHERE username = ?";
            PreparedStatement pst = conn.prepareStatement(updateQuery);
            pst.setString(1, hashPassword(newPassword));
            pst.setString(2, username);
            
            int result = pst.executeUpdate();
            
            if (result > 0) {
                System.out.println("\n[OK] Password reset successfully for user: " + username);
            } else {
                System.out.println("\n[ERROR] User not found: " + username);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error resetting password: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main menu for easy access to all utilities
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n================================================");
            System.out.println(" DATABASE UTILITY");
            System.out.println("================================================");
            System.out.println("\n[1] Create Default Admin Account");
            System.out.println("[2] List All Manager/Admin Accounts");
            System.out.println("[3] Create New Manager Account");
            System.out.println("[4] Generate Password Hash");
            System.out.println("[5] Reset User Password");
            System.out.println("[0] Exit");
            System.out.println("\n------------------------------------------------");
            System.out.print("Select option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    createDefaultAdmin();
                    break;
                case "2":
                    listManagerAccounts();
                    break;
                case "3":
                    createManagerAccount();
                    break;
                case "4":
                    generatePasswordHash();
                    break;
                case "5":
                    resetPassword();
                    break;
                case "0":
                    System.out.println("\nGoodbye!");
                    return;
                default:
                    System.out.println("\n[ERROR] Invalid option!");
            }
        }
    }
}