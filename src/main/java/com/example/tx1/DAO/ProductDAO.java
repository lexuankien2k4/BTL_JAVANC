package com.example.tx1.DAO;

import com.example.tx1.Connection.JDBCUtil;
import com.example.tx1.Entity.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        Connection conn = JDBCUtil.getConnection();
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM product");
                while (rs.next()) {
                    Product p = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("price"),
                            rs.getString("imageUrl"),
                            rs.getString("description")
                    );
                    list.add(p);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void insertProduct(Product p) {
        Connection conn = JDBCUtil.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO product (name, price, imageUrl, description) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, p.getName());
                stmt.setString(2, p.getPrice());
                stmt.setString(3, p.getImageUrl());
                stmt.setString(4, p.getDescription());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateProduct(Product p) {
        Connection conn = JDBCUtil.getConnection();
        if (conn != null) {
            String sql = "UPDATE product SET name=?, price=?, imageUrl=?, description=? WHERE id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, p.getName());
                stmt.setString(2, p.getPrice());
                stmt.setString(3, p.getImageUrl());
                stmt.setString(4, p.getDescription());
                stmt.setInt(5, p.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteProduct(int id) {
        Connection conn = JDBCUtil.getConnection();
        if (conn != null) {
            String sql = "DELETE FROM product WHERE id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static List<Product> searchProducts(String keyword) {
        List<Product> list = new ArrayList<>();
        Connection conn = JDBCUtil.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM product WHERE name LIKE ? OR description LIKE ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String pattern = "%" + keyword + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Product p = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("price"),
                            rs.getString("imageUrl"),
                            rs.getString("description")
                    );
                    list.add(p);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}