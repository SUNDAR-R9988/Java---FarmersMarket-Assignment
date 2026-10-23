package com.farmersmarket.model;

public class UPIPayment extends Payment {
    private String upiId;
    private String upiRefNo;

    public UPIPayment(String paymentId, String orderId, double amount, String paymentDate, String upiId, String upiRefNo) {
        super(paymentId, orderId, amount, paymentDate);
        this.upiId = upiId;
        this.upiRefNo = upiRefNo;
    }

    public String getUpiId() {
        return upiId;
    }

    public String getUpiRefNo() {
        return upiRefNo;
    }

    @Override
    public boolean processPayment() {
        // Simulates instant UPI transfer
        setPaymentStatus("SETTLED");
        return true;
    }

    @Override
    public String getPaymentMode() {
        return "UPI";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | UPI ID: %s | UTR: %s", upiId, upiRefNo);
    }
}
