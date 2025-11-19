/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jumnert
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    
    /**
     * Get all active items by category
     */
    public ResultSet getItemsByCategory(int categoryId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM items WHERE category_id = ? AND is_available = TRUE " +
                      "ORDER BY name";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, categoryId);
        return pst.executeQuery();
    }
    
    /**
     * Get all categories
     */
    public ResultSet getAllCategories() throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM categories WHERE is_active = TRUE " +
                      "ORDER BY display_order";
        PreparedStatement pst = conn.prepareStatement(query);
        return pst.executeQuery();
    }
    
    /**
     * Update item stock
     */
    public void updateItemStock(int itemId, int quantity) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String query = "UPDATE items SET stock_quantity = stock_quantity - ? " +
                          "WHERE item_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, quantity);
            pst.setInt(2, itemId);
            pst.executeUpdate();
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    /**
     * Get low stock items
     */
    public ResultSet getLowStockItems() throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM low_stock_items";
        PreparedStatement pst = conn.prepareStatement(query);
        return pst.executeQuery();
    }
}
