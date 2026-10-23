package com.farmersmarket.model;

public abstract class Payment {
    private String paymentId;
    private String orderId;
    private double amount;
    private String paymentDate;
    private String paymentStatus; // SETTLED, PENDING, FAILED

    public Payment(String paymentId, String orderId, double amount, String paymentDate) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentStatus = "PENDING";
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // Polymorphic methods
    public abstract boolean processPayment();
    public abstract String getPaymentMode();

    @Override
    public String toString() {
        return String.format("Payment ID: %s | Order: %s | Mode: %s | Amount: ₹%.2f | Status: %s | Date: %s",
                paymentId, orderId, getPaymentMode(), amount, paymentStatus, paymentDate);
    }
}
