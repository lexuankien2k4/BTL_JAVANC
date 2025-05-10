package com.example.tx1.DAO;

import com.example.tx1.Connection.JDBCUtil;
import com.example.tx1.Entity.Order;
import java.sql.*;

public class OrderDAO {
    public static int insertOrder(Order order) {
        Connection conn = JDBCUtil.getConnection();
        int generatedId = -1;
        if (conn != null) {
            String sql = "INSERT INTO lanchi_db.order (user_id, payment_id, order_date, status, total_price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, order.getUserId());
                stmt.setInt(2, order.getPaymentId());
                stmt.setTimestamp(3, new Timestamp(order.getOrderDate().getDayOfYear()));
                stmt.setString(4, order.getStatus());
                stmt.setDouble(5, order.getTotalPrice());

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
