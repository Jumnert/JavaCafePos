import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.table.DefaultTableModel;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jumnert
 */
public class OrderManager {
    
    private int currentUserId = 1; // Default to admin, should be set after login
    
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
    
    /**
     * Save complete order with all items
     * @param model
     * @param paymentMethod
     * @param amountPaid
     * @param changeAmount
     * @return 
     * @throws java.sql.SQLException 
     */
    public int saveOrder(DefaultTableModel model, String paymentMethod, 
                         double amountPaid, double changeAmount) throws SQLException {
        
        Connection conn = null;
        int orderId = 0;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Calculate totals
            double subtotal = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                subtotal += Double.parseDouble(model.getValueAt(i, 2).toString());
            }
            double taxAmount = subtotal * 0.039;
            double totalAmount = subtotal + taxAmount;
            
            // Get payment method ID
            int paymentMethodId = getPaymentMethodId(conn, paymentMethod);
            
            // Generate order number
            String orderNumber = generateOrderNumber(conn);
            
            // Insert order
            String insertOrder = "INSERT INTO orders " +
                "(order_number, user_id, payment_method_id, subtotal, tax_amount, " +
                "discount_amount, total_amount, amount_paid, change_amount, " +
                "order_date, order_time) " +
                "VALUES (?, ?, ?, ?, ?, 0, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstOrder = conn.prepareStatement(insertOrder, 
                Statement.RETURN_GENERATED_KEYS);
            pstOrder.setString(1, orderNumber);
            pstOrder.setInt(2, currentUserId);
            pstOrder.setInt(3, paymentMethodId);
            pstOrder.setDouble(4, subtotal);
            pstOrder.setDouble(5, taxAmount);
            pstOrder.setDouble(6, totalAmount);
            pstOrder.setDouble(7, amountPaid);
            pstOrder.setDouble(8, changeAmount);
            pstOrder.setDate(9, Date.valueOf(LocalDate.now()));
            pstOrder.setTime(10, Time.valueOf(LocalTime.now()));
            pstOrder.executeUpdate();
            
            // Get generated order ID
            ResultSet rs = pstOrder.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            
            // Insert order items
            String insertOrderItem = "INSERT INTO order_items " +
                "(order_id, item_id, item_name, quantity, unit_price, subtotal) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstItem = conn.prepareStatement(insertOrderItem);
            
            for (int i = 0; i < model.getRowCount(); i++) {
                String itemName = model.getValueAt(i, 0).toString();
                int quantity = Integer.parseInt(model.getValueAt(i, 1).toString());
                double price = Double.parseDouble(model.getValueAt(i, 2).toString());
                
                // Get item ID from database
                int itemId = getItemIdByName(conn, itemName);
                
                pstItem.setInt(1, orderId);
                pstItem.setInt(2, itemId);
                pstItem.setString(3, itemName);
                pstItem.setInt(4, quantity);
                pstItem.setDouble(5, price / quantity); // unit price
                pstItem.setDouble(6, price);
                pstItem.addBatch();
            }
            
            pstItem.executeBatch();
            
            conn.commit();
            System.out.println("Order saved successfully! Order ID: " + orderId + 
                             ", Order Number: " + orderNumber);
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return orderId;
    }
    
    /**
     * Generate unique order number
     */
    private String generateOrderNumber(Connection conn) throws SQLException {
        String orderNumber = "";
        String today = LocalDate.now().toString().replace("-", "");
        
        String query = "SELECT COUNT(*) FROM orders WHERE order_date = CURDATE()";
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                orderNumber = String.format("ORD-%s-%04d", today, count);
            }
        }
        
        return orderNumber;
    }
    
    /**
     * Get payment method ID by name
     */
    private int getPaymentMethodId(Connection conn, String methodName) throws SQLException {
        String query = "SELECT payment_method_id FROM payment_methods WHERE method_name = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, methodName);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("payment_method_id");
            }
        }
        // Default to Cash if not found
        return 1;
    }
    
    /**
     * Get item ID by name
     */
    private int getItemIdByName(Connection conn, String itemName) throws SQLException {
        String query = "SELECT item_id FROM items WHERE name = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, itemName);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("item_id");
            }
        }
        throw new SQLException("Item not found: " + itemName);
    }
    
    /**
     * Get today's sales report
     */
    public ResultSet getTodaySales() throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM daily_sales_summary WHERE order_date = CURDATE()";
        PreparedStatement pst = conn.prepareStatement(query);
        return pst.executeQuery();
    }
    
    /**
     * Get sales report by date range
     */
    public ResultSet getSalesByDateRange(LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM daily_sales_summary " +
                      "WHERE order_date BETWEEN ? AND ? " +
                      "ORDER BY order_date DESC";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setDate(1, Date.valueOf(startDate));
        pst.setDate(2, Date.valueOf(endDate));
        return pst.executeQuery();
    }
    
    /**
     * Get popular items
     */
    public ResultSet getPopularItems(int limit) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM popular_items LIMIT ?";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, limit);
        return pst.executeQuery();
    }
}
