package com.farmersmarket.model;

public class OrderItem {
    private String orderItemId;
    private Product product; // Links directly to a vendor's product (Business Rule 5)
    private int quantity;
    private double unitPrice;

    public OrderItem(String orderItemId, Product product, int quantity) {
        this.orderItemId = orderItemId;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getUnitPrice();
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return this.quantity * this.unitPrice;
    }

    @Override
    public String toString() {
        return String.format("%-10s %-20s %-20s %5d %10.2f %12.2f",
                product.getProductId(),
                product.getName(),
                product.getVendor().getName(),
                quantity,
                unitPrice,
                getSubtotal());
    }
}
