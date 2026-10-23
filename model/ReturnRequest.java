package com.farmersmarket.model;

public class ReturnRequest {
    private String returnId;
    private String orderId;
    private String productId;
    private int requestedQuantity;
    private String reason;
    private String requestDate;
    private String status; // PENDING, APPROVED, REJECTED
    private String decisionRemarks;
    private double refundAmount;

    public ReturnRequest(String returnId, String orderId, String productId, int requestedQuantity,
                         String reason, String requestDate, double refundAmount) {
        this.returnId = returnId;
        this.orderId = orderId;
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.reason = reason;
        this.requestDate = requestDate;
        this.status = "PENDING";
        this.decisionRemarks = "Awaiting verification";
        this.refundAmount = refundAmount;
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(int requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDecisionRemarks() {
        return decisionRemarks;
    }

    public void setDecisionRemarks(String decisionRemarks) {
        this.decisionRemarks = decisionRemarks;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    @Override
    public String toString() {
        return String.format("Return ID: %s | Order: %s | Product: %s | Qty: %d | Refund: ₹%.2f | Status: %s | Remarks: %s",
                returnId, orderId, productId, requestedQuantity, refundAmount, status, decisionRemarks);
    }
}
