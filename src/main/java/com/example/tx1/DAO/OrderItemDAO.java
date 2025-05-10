package com.example.tx1.DAO;
import com.example.tx1.Connection.JDBCUtil;
import com.example.tx1.Entity.OrderItem;
import java.sql.*;

public class OrderItemDAO {
    public static int insertOrderItem(OrderItem orderItem) {
        Connection conn = JDBCUtil.getConnection();
        int generatedId = -1;
        if (conn != null) {
            String sql = "INSERT INTO lanchi_db.order_item (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, orderItem.getOrderId());
                stmt.setInt(2, orderItem.getProductId());
                stmt.setInt(3, orderItem.getQuantity());
                stmt.setDouble(4, orderItem.getPrice());

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return generatedId;
    }
}
