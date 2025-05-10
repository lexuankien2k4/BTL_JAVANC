package com.example.tx1.Entity;

import java.time.LocalDateTime;
import java.util.Date;

public class Order {
    private int id;
    private int userId;
    private int paymentId;
    private LocalDateTime orderDate;
    private String status;
    private double totalPrice;

    public Order() {

    }

    public Order(int id, int userId, int paymentId, LocalDateTime orderDate, String status, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.paymentId = paymentId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public Order(int id, int currentUserId, int paymentId, Date currentDate, String pending, double totalAmount) {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
