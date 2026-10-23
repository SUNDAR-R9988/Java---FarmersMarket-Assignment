package com.farmersmarket.model;

public class Delivery {
    private String deliveryId;
    private String orderId;
    private String deliveryAddress;
    private String contactPhone;
    private String scheduledDate;
    private String deliveryStatus; // SCHEDULED, OUT_FOR_DELIVERY, DELIVERED
    private String trackingNotes;

    public Delivery(String deliveryId, String orderId, String deliveryAddress, String contactPhone, String scheduledDate) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.contactPhone = contactPhone;
        this.scheduledDate = scheduledDate;
        this.deliveryStatus = "SCHEDULED";
        this.trackingNotes = "Order packed and assigned to delivery agent.";
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getTrackingNotes() {
        return trackingNotes;
    }

    public void setTrackingNotes(String trackingNotes) {
        this.trackingNotes = trackingNotes;
    }

    @Override
    public String toString() {
        return String.format("Delivery ID: %s | Order: %s | Status: %s | Date: %s | Address: %s | Phone: %s | Notes: %s",
                deliveryId, orderId, deliveryStatus, scheduledDate, deliveryAddress, contactPhone, trackingNotes);
    }
}
