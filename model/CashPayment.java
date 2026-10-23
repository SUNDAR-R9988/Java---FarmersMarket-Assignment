package com.farmersmarket.model;

public class CashPayment extends Payment {
    private double tenderedAmount;
    private double changeReturned;

    public CashPayment(String paymentId, String orderId, double amount, String paymentDate, double tenderedAmount) {
        super(paymentId, orderId, amount, paymentDate);
        this.tenderedAmount = tenderedAmount;
        this.changeReturned = Math.max(0.0, tenderedAmount - amount);
    }

    public double getTenderedAmount() {
        return tenderedAmount;
    }

    public double getChangeReturned() {
        return changeReturned;
    }

    @Override
    public boolean processPayment() {
        if (tenderedAmount >= getAmount()) {
            setPaymentStatus("SETTLED");
            return true;
        } else {
            setPaymentStatus("FAILED");
            return false;
        }
    }

    @Override
    public String getPaymentMode() {
        return "CASH";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Tendered: ₹%.2f | Change: ₹%.2f", tenderedAmount, changeReturned);
    }
}
