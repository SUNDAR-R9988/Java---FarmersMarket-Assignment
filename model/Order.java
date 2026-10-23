package com.farmersmarket.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private String customerName;
    private String customerContact;
    private String orderDate;
    private List<OrderItem> items; // Composition: Order contains OrderItems
    private Payment payment;       // Aggregation: Order associates with Payment
    private Delivery delivery;     // Aggregation: Order associates with Delivery
    private List<ReturnRequest> returnRequests;
    private String orderStatus;    // CONFIRMED, CANCELLED, DELIVERED

    public Order(String orderId, String customerName, String customerContact, String orderDate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.orderDate = orderDate;
        this.items = new ArrayList<>();
        this.returnRequests = new ArrayList<>();
        this.orderStatus = "CONFIRMED";
        this.payment = null;
        this.delivery = null;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        if (delivery != null) {
            this.orderStatus = "DELIVERY_SCHEDULED";
        }
    }

    public List<ReturnRequest> getReturnRequests() {
        return returnRequests;
    }

    public void addReturnRequest(ReturnRequest request) {
        this.returnRequests.add(request);
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public boolean isPaymentSettled() {
        return payment != null && "SETTLED".equalsIgnoreCase(payment.getPaymentStatus());
    }

    public OrderItem findItemByProductId(String productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getProductId().equalsIgnoreCase(productId)) {
                return item;
            }
        }
        return null;
    }

    public int getReturnedQuantityForProduct(String productId) {
        int sum = 0;
        for (ReturnRequest req : returnRequests) {
            if (req.getProductId().equalsIgnoreCase(productId) && "APPROVED".equalsIgnoreCase(req.getStatus())) {
                sum += req.getRequestedQuantity();
            }
        }
        return sum;
    }
}
