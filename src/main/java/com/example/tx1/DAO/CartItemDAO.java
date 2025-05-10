package com.example.tx1.DAO;

import com.example.tx1.Connection.JDBCUtil;
import com.example.tx1.Entity.CartItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartItemDAO {
    public static void addOrUpdateCartItem(CartItem item) {
        Connection conn = JDBCUtil.getConnection();
        try {
            // Kiểm tra sản phẩm đã có trong giỏ chưa
            String checkSql = "SELECT * FROM cart_items WHERE user_id = ? AND product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, item.getUserId());
            checkStmt.setInt(2, item.getProductId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int newQuantity = rs.getInt("quantity") + item.getQuantity();
                String updateSql = "UPDATE cart_items SET quantity = ?, price = ?, update_at = NOW() WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, newQuantity);
                updateStmt.setInt(2, item.getPrice());
                updateStmt.setInt(3, rs.getInt("id"));
                updateStmt.executeUpdate();
            } else {
                String insertSql = "INSERT INTO cart_items (user_id, product_id, quantity, price, create_at, update_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, item.getUserId());
                insertStmt.setInt(2, item.getProductId());
                insertStmt.setInt(3, item.getQuantity());
                insertStmt.setInt(4, item.getPrice());
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<CartItem> getCartByUserId(int userId) {
        List<CartItem> list = new ArrayList<>();
        Connection conn = JDBCUtil.getConnection();
        try {
            String sql = "SELECT * FROM cart_items WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getInt("price"),
                        rs.getTimestamp("create_at"),
                        rs.getTimestamp("update_at")
                );
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void removeCartItem(int id) {
        Connection conn = JDBCUtil.getConnection();
        try {
            String sql = "DELETE FROM cart_items WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearCartByUser(int userId) {
        Connection conn = JDBCUtil.getConnection();
        try {
            String sql = "DELETE FROM cart_items WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
