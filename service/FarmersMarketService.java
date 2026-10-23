package com.farmersmarket.service;

import com.farmersmarket.exception.*;
import com.farmersmarket.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FarmersMarketService {
    private Map<String, Vendor> vendors;
    private Map<String, Product> products;
    private Map<String, Order> orders;
    private Map<String, Payment> payments;
    private Map<String, Delivery> deliveries;
    private Map<String, ReturnRequest> returnRequests;

    private int vendorCounter = 100;
    private int productCounter = 200;
    private int orderCounter = 1000;
    private int paymentCounter = 5000;
    private int deliveryCounter = 7000;
    private int returnCounter = 9000;

    public FarmersMarketService() {
        this.vendors = new LinkedHashMap<>();
        this.products = new LinkedHashMap<>();
        this.orders = new LinkedHashMap<>();
        this.payments = new LinkedHashMap<>();
        this.deliveries = new LinkedHashMap<>();
        this.returnRequests = new LinkedHashMap<>();
        seedInitialData();
    }

    private void seedInitialData() {
        // Sample vendor 1
        Vendor v1 = new Vendor("VND-" + (++vendorCounter), "Green Earth Organic Farm", "Ooty Hills, TN", "9876543210");
        vendors.put(v1.getVendorId(), v1);
        Product p1 = new Product("PRD-" + (++productCounter), "Organic Apples", "Fruits", 180.00, 25, v1);
        Product p2 = new Product("PRD-" + (++productCounter), "Fresh Broccoli", "Vegetables", 65.00, 40, v1);
        Product p3 = new Product("PRD-" + (++productCounter), "Wild Forest Honey", "Organic", 320.00, 15, v1);
        products.put(p1.getProductId(), p1);
        products.put(p2.getProductId(), p2);
        products.put(p3.getProductId(), p3);

        // Sample vendor 2
        Vendor v2 = new Vendor("VND-" + (++vendorCounter), "Valley Sunrise Dairy & Poultry", "Pollachi Plains, TN", "9123456789");
        vendors.put(v2.getVendorId(), v2);
        Product p4 = new Product("PRD-" + (++productCounter), "Farm Fresh Cow Milk (1L)", "Dairy", 55.00, 50, v2);
        Product p5 = new Product("PRD-" + (++productCounter), "Country Eggs (Pack of 6)", "Poultry", 75.00, 30, v2);
        Product p6 = new Product("PRD-" + (++productCounter), "Artisanal Cow Ghee (500ml)", "Dairy", 420.00, 10, v2);
        products.put(p4.getProductId(), p4);
        products.put(p5.getProductId(), p5);
        products.put(p6.getProductId(), p6);
    }

    // 1. Add Vendor
    public Vendor addVendor(String name, String location, String contact) {
        String vendorId = "VND-" + (++vendorCounter);
        Vendor vendor = new Vendor(vendorId, name, location, contact);
        vendors.put(vendorId, vendor);
        return vendor;
    }

    // 2. Add Product (linked directly to a vendor)
    public Product addProduct(String name, String category, double price, int stock, String vendorId)
            throws ResourceNotFoundException {
        Vendor vendor = vendors.get(vendorId);
        if (vendor == null) {
            throw new ResourceNotFoundException("Vendor with ID '" + vendorId + "' does not exist.");
        }
        String productId = "PRD-" + (++productCounter);
        Product product = new Product(productId, name, category, price, stock, vendor);
        products.put(productId, product);
        return product;
    }

    // 3. Place Order (Enforces Business Rules 1, 2, 5)
    public Order placeOrder(String customerName, String customerPhone, Map<String, Integer> cartItems)
            throws ResourceNotFoundException, InsufficientStockException {
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty.");
        }

        // Validate all products and check availability first (Business Rule 1)
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            String prodId = entry.getKey();
            int qty = entry.getValue();
            Product prod = products.get(prodId);
            if (prod == null) {
                throw new ResourceNotFoundException("Product with ID '" + prodId + "' not found.");
            }
            if (qty > prod.getAvailableStock()) {
                throw new InsufficientStockException("Cannot place order: Product '" + prod.getName()
                        + "' has only " + prod.getAvailableStock() + " units available (Requested: " + qty + ") from Vendor '"
                        + prod.getVendor().getName() + "'.");
            }
        }

        // Create Order
        String orderId = "ORD-" + (++orderCounter);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        Order order = new Order(orderId, customerName, customerPhone, today);

        int itemSeq = 1;
        Set<Vendor> affectedVendors = new HashSet<>();

        // Deduct stock and link items (Business Rules 2 & 5)
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            String prodId = entry.getKey();
            int qty = entry.getValue();
            Product prod = products.get(prodId);

            prod.decreaseStock(qty); // Business Rule 2
            affectedVendors.add(prod.getVendor());

            OrderItem item = new OrderItem("ITEM-" + (itemSeq++), prod, qty);
            order.addItem(item);
        }

        orders.put(orderId, order);

        // Display Generated Invoice & Vendor Inventory Updates
        displayOrderInvoice(order);
        displayVendorStockUpdates("ORDER CONFIRMATION (" + orderId + ")", affectedVendors);

        return order;
    }

    // 4. Record Payment
    public Payment recordPayment(String orderId, Payment payment)
            throws ResourceNotFoundException, MarketException {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID '" + orderId + "' does not exist.");
        }
        if (order.isPaymentSettled()) {
            throw new MarketException("Payment has already been settled for Order " + orderId + ".");
        }

        boolean success = payment.processPayment();
        if (!success) {
            throw new MarketException("Payment transaction failed. Order amount not settled.");
        }

        order.setPayment(payment);
        payments.put(payment.getPaymentId(), payment);
        return payment;
    }

    // 5. Schedule Delivery (Enforces Business Rule 3)
    public Delivery scheduleDelivery(String orderId, String address, String contactPhone, String date)
            throws ResourceNotFoundException, PaymentPendingException {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID '" + orderId + "' does not exist.");
        }

        // Business Rule 3: Payments must be settled before scheduling delivery
        if (!order.isPaymentSettled()) {
            throw new PaymentPendingException("Payment Pending! Order " + orderId 
                    + " has not been settled yet. Settle payment before scheduling delivery.");
        }

        if (order.getDelivery() != null) {
            throw new IllegalArgumentException("Delivery has already been scheduled for Order " + orderId + ".");
        }

        String deliveryId = "DEL-" + (++deliveryCounter);
        Delivery delivery = new Delivery(deliveryId, orderId, address, contactPhone, date);
        order.setDelivery(delivery);
        deliveries.put(deliveryId, delivery);
        return delivery;
    }

    // 6. Request Return (Enforces Business Rules 2 & 4)
    public ReturnRequest requestReturn(String orderId, String productId, int quantity, String reason)
            throws ResourceNotFoundException, InvalidReturnException {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID '" + orderId + "' does not exist.");
        }

        OrderItem purchasedItem = order.findItemByProductId(productId);
        if (purchasedItem == null) {
            throw new InvalidReturnException("Product ID '" + productId + "' was never purchased in Order " + orderId + ".");
        }

        // Business Rule 4: Returns are allowed within policy and cannot exceed purchased quantity
        int alreadyReturned = order.getReturnedQuantityForProduct(productId);
        int maxEligible = purchasedItem.getQuantity() - alreadyReturned;

        if (quantity <= 0) {
            throw new InvalidReturnException("Return quantity must be greater than zero.");
        }

        if (quantity > maxEligible) {
            throw new InvalidReturnException("Return rejected! Requested quantity (" + quantity 
                    + ") exceeds eligible quantity (" + maxEligible + "). Total purchased: " 
                    + purchasedItem.getQuantity() + ", Already returned: " + alreadyReturned + ".");
        }

        // Policy rule: Perishable Dairy items cannot be returned for "Buyer remorse / Change of mind"
        Product product = purchasedItem.getProduct();
        boolean approved = true;
        String decisionRemarks;

        if ("Dairy".equalsIgnoreCase(product.getCategory()) && reason.toLowerCase().contains("change of mind")) {
            approved = false;
            decisionRemarks = "REJECTED as per Market Policy: Perishable dairy items cannot be returned due to change of mind.";
        } else {
            decisionRemarks = "APPROVED: Meets return policy conditions. Quality check passed.";
        }

        String returnId = "RET-" + (++returnCounter);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        double refund = approved ? (quantity * purchasedItem.getUnitPrice()) : 0.0;

        ReturnRequest returnReq = new ReturnRequest(returnId, orderId, productId, quantity, reason, today, refund);
        returnReq.setStatus(approved ? "APPROVED" : "REJECTED");
        returnReq.setDecisionRemarks(decisionRemarks);

        order.addReturnRequest(returnReq);
        returnRequests.put(returnId, returnReq);

        // Business Rule 2: Stock increases on approved returns
        if (approved) {
            product.increaseStock(quantity);
            Set<Vendor> affected = new HashSet<>();
            affected.add(product.getVendor());
            displayVendorStockUpdates("APPROVED RETURN RESTOCK (" + returnId + ")", affected);
        }

        return returnReq;
    }

    // 7. Display Vendor-Wise Inventory
    public void displayInventory() {
        System.out.println("\n=========================================================================================");
        System.out.println("                         FARMERS' MARKET: VENDOR-WISE INVENTORY                          ");
        System.out.println("=========================================================================================");
        if (vendors.isEmpty()) {
            System.out.println("No vendors registered in the market.");
            return;
        }

        for (Vendor v : vendors.values()) {
            System.out.println("\n-----------------------------------------------------------------------------------------");
            System.out.printf("Vendor ID   : %-12s | Name     : %s\n", v.getVendorId(), v.getName());
            System.out.printf("Farm Origin : %-12s | Contact  : %s\n", v.getFarmLocation(), v.getContactNumber());
            System.out.println("-----------------------------------------------------------------------------------------");
            System.out.printf("%-10s %-24s %-15s %10s %14s\n", "PROD ID", "PRODUCT NAME", "CATEGORY", "UNIT PRICE", "STOCK IN HAND");
            System.out.println("-----------------------------------------------------------------------------------------");
            if (v.getProducts().isEmpty()) {
                System.out.println("   No products registered for this vendor.");
            } else {
                for (Product p : v.getProducts()) {
                    System.out.printf("%-10s %-24s %-15s %10.2f %14d\n",
                            p.getProductId(), p.getName(), p.getCategory(), p.getUnitPrice(), p.getAvailableStock());
                }
            }
        }
        System.out.println("=========================================================================================\n");
    }

    // Display Vendor Inventory Updates after specific events (Order confirmation, Return approval)
    public void displayVendorStockUpdates(String eventTitle, Set<Vendor> affectedVendors) {
        System.out.println("\n>>> [EVENT] VENDOR-WISE INVENTORY UPDATE AFTER " + eventTitle);
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("%-12s %-25s %-10s %-20s %12s\n", "VENDOR ID", "VENDOR NAME", "PROD ID", "PRODUCT NAME", "CURRENT STOCK");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Vendor v : affectedVendors) {
            for (Product p : v.getProducts()) {
                System.out.printf("%-12s %-25s %-10s %-20s %12d\n",
                        v.getVendorId(), v.getName(), p.getProductId(), p.getName(), p.getAvailableStock());
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    // Display Customer Order History and Totals
    public void displayCustomerOrderHistory() {
        System.out.println("\n=========================================================================================");
        System.out.println("                             ALL CUSTOMER ORDER HISTORY & TOTALS                         ");
        System.out.println("=========================================================================================");
        if (orders.isEmpty()) {
            System.out.println("No orders have been placed yet.");
            return;
        }

        double cumulativeRevenue = 0.0;
        for (Order o : orders.values()) {
            double total = o.calculateTotal();
            cumulativeRevenue += total;
            String payStatus = (o.getPayment() != null) ? o.getPayment().getPaymentStatus() + " (" + o.getPayment().getPaymentMode() + ")" : "UNPAID";
            String delStatus = (o.getDelivery() != null) ? o.getDelivery().getDeliveryStatus() : "NOT SCHEDULED";

            System.out.printf("\nOrder ID: %-10s | Date: %-11s | Customer: %-15s | Phone: %s\n",
                    o.getOrderId(), o.getOrderDate(), o.getCustomerName(), o.getCustomerContact());
            System.out.printf("Order Total: ₹%-10.2f | Payment: %-22s | Delivery: %s\n",
                    total, payStatus, delStatus);
            System.out.println("Items Ordered:");
            for (OrderItem itm : o.getItems()) {
                System.out.printf("   - %-20s x %2d units @ ₹%7.2f (Vendor: %s) = ₹%.2f\n",
                        itm.getProduct().getName(), itm.getQuantity(), itm.getUnitPrice(),
                        itm.getProduct().getVendor().getName(), itm.getSubtotal());
            }
            if (!o.getReturnRequests().isEmpty()) {
                System.out.println("Returns / Refund Requests:");
                for (ReturnRequest req : o.getReturnRequests()) {
                    System.out.printf("   * Return [%s] for %s - Qty: %d, Status: %s, Refund: ₹%.2f (Reason: %s)\n",
                            req.getReturnId(), req.getProductId(), req.getRequestedQuantity(), req.getStatus(), req.getRefundAmount(), req.getReason());
                }
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("TOTAL MARKET ORDER REVENUE RECORDED: ₹%.2f across %d orders\n", cumulativeRevenue, orders.size());
        System.out.println("=========================================================================================\n");
    }

    // Invoice generator
    public void displayOrderInvoice(Order order) {
        System.out.println("\n=========================================================================================");
        System.out.println("                               FARMERS' MARKET ORDER INVOICE                             ");
        System.out.println("=========================================================================================");
        System.out.printf("Invoice No   : INV-%s               Date      : %s\n", order.getOrderId(), order.getOrderDate());
        System.out.printf("Customer Name: %-28s Contact No : %s\n", order.getCustomerName(), order.getCustomerContact());
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-20s %-20s %5s %10s %12s\n", "ITEM ID", "PRODUCT NAME", "VENDOR", "QTY", "PRICE(₹)", "SUBTOTAL(₹)");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (OrderItem itm : order.getItems()) {
            System.out.println(itm);
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("%60s TOTAL AMOUNT: ₹%10.2f\n", "", order.calculateTotal());
        System.out.printf("%60s PAYMENT STATUS: %-10s\n", "", (order.getPayment() != null ? order.getPayment().getPaymentStatus() : "PENDING"));
        System.out.printf("%60s DELIVERY STATUS: %-10s\n", "", (order.getDelivery() != null ? order.getDelivery().getDeliveryStatus() : "NOT SCHEDULED"));
        System.out.println("=========================================================================================\n");
    }

    // Getters for maps and next IDs
    public Map<String, Vendor> getVendors() { return vendors; }
    public Map<String, Product> getProducts() { return products; }
    public Map<String, Order> getOrders() { return orders; }
    public String generateNextPaymentId() { return "PAY-" + (++paymentCounter); }
}
