package com.farmersmarket.model;

import com.farmersmarket.exception.InsufficientStockException;

public class Product {
    private String productId;
    private String name;
    private String category;
    private double unitPrice;
    private int availableStock;
    private Vendor vendor; // Direct link to vendor (Business Rule 5)

    public Product(String productId, String name, String category, double unitPrice, int availableStock, Vendor vendor) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.availableStock = availableStock;
        this.vendor = vendor;
        if (vendor != null) {
            vendor.addProduct(this);
        }
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    // Business Rule 2: Stock decreases on order confirmation
    public void decreaseStock(int quantity) throws InsufficientStockException {
        if (quantity > this.availableStock) {
            throw new InsufficientStockException(
                "Insufficient stock for " + name + " (ID: " + productId + "). Requested: " 
                + quantity + ", Available: " + availableStock + " from vendor " + vendor.getName());
        }
        this.availableStock -= quantity;
    }

    // Business Rule 2: Stock increases on approved returns
    public void increaseStock(int quantity) {
        this.availableStock += quantity;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-18s | Category: %-12s | Price: ₹%7.2f | Stock: %3d units | Vendor: %s",
                productId, name, category, unitPrice, availableStock, vendor != null ? vendor.getName() : "N/A");
    }
}
