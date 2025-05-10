package com.example.tx1.DAO;

import com.example.tx1.Connection.JDBCUtil;
import com.example.tx1.Entity.Payment;
import java.sql.*;

public class PaymentDAO {
    public static int insertPayment(Payment payment) {
        Connection conn = JDBCUtil.getConnection();
        int generatedId = -1;
        if (conn != null) {
            String sql = "INSERT INTO lanchi_db.payment (user_id, payment_date, payment_method, amount, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, payment.getUserId());
                stmt.setTimestamp(2, new Timestamp(payment.getPaymentDate().getTime()));
                stmt.setString(3, payment.getPaymentMethod());
                stmt.setDouble(4, payment.getAmount());
                stmt.setString(5, payment.getStatus());

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
