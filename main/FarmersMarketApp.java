package com.farmersmarket.main;

import com.farmersmarket.exception.*;
import com.farmersmarket.model.*;
import com.farmersmarket.service.FarmersMarketService;
import com.farmersmarket.util.ValidationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FarmersMarketApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final FarmersMarketService marketService = new FarmersMarketService();

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("   FARMERS' MARKET ORDERS & PAYMENTS MANAGEMENT SYSTEM - KCE IT DEPT     ");
        System.out.println("==========================================================================");

        boolean exit = false;
        while (!exit) {
            displayMenu();
            int choice = ValidationUtil.readMenuChoice(scanner, 1, 9);
            try {
                switch (choice) {
                    case 1:
                        handleAddVendor();
                        break;
                    case 2:
                        handleAddProduct();
                        break;
                    case 3:
                        handlePlaceOrder();
                        break;
                    case 4:
                        handleRecordPayment();
                        break;
                    case 5:
                        handleScheduleDelivery();
                        break;
                    case 6:
                        handleRequestReturn();
                        break;
                    case 7:
                        marketService.displayInventory();
                        break;
                    case 8:
                        marketService.displayCustomerOrderHistory();
                        break;
                    case 9:
                        System.out.println("\nThank you for using the Farmers' Market System. Goodbye!");
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose again.");
                }
            } catch (MarketException e) {
                System.out.println("\n[BUSINESS RULE VIOLATION / APPLICATION ERROR]: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n[SYSTEM ERROR]: An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|                   FARMERS' MARKET MAIN MENU                 |");
        System.out.println("+-------------------------------------------------------------+");
        System.out.println("| 1. Add Vendor                                               |");
        System.out.println("| 2. Add Product                                              |");
        System.out.println("| 3. Place Order                                              |");
        System.out.println("| 4. Record Payment                                           |");
        System.out.println("| 5. Schedule Delivery                                        |");
        System.out.println("| 6. Request Return                                           |");
        System.out.println("| 7. Display Inventory                                        |");
        System.out.println("| 8. Customer Order History & Invoices                        |");
        System.out.println("| 9. Exit                                                     |");
        System.out.println("+-------------------------------------------------------------+");
    }

    private static void handleAddVendor() {
        System.out.println("\n--- [1] ADD NEW VENDOR ---");
        String name = ValidationUtil.readNonEmptyString(scanner, "Enter Vendor / Farm Name : ");
        String location = ValidationUtil.readNonEmptyString(scanner, "Enter Farm Location      : ");
        String phone = ValidationUtil.readPhoneNumber(scanner, "Enter Contact Phone (10 digits): ");

        Vendor vendor = marketService.addVendor(name, location, phone);
        System.out.println(">> Success! Vendor registered successfully.");
        System.out.println(">> Details: " + vendor);
    }

    private static void handleAddProduct() throws ResourceNotFoundException {
        System.out.println("\n--- [2] ADD PRODUCT TO VENDOR INVENTORY ---");
        if (marketService.getVendors().isEmpty()) {
            System.out.println(">> No vendors available. Please add a vendor first.");
            return;
        }

        System.out.println("Available Vendors:");
        for (Vendor v : marketService.getVendors().values()) {
            System.out.println("   " + v);
        }

        String vendorId = ValidationUtil.readNonEmptyString(scanner, "Enter Vendor ID: ");
        String name = ValidationUtil.readNonEmptyString(scanner, "Enter Product Name: ");
        String category = ValidationUtil.readNonEmptyString(scanner, "Enter Category (e.g., Fruits, Vegetables, Dairy, Organic): ");
        double price = ValidationUtil.readPositiveDouble(scanner, "Enter Unit Price (₹): ");
        int stock = ValidationUtil.readPositiveInt(scanner, "Enter Initial Stock Quantity: ");

        Product product = marketService.addProduct(name, category, price, stock, vendorId);
        System.out.println(">> Success! Product added to vendor inventory.");
        System.out.println(">> Product: " + product);
    }

    private static void handlePlaceOrder() throws ResourceNotFoundException, InsufficientStockException {
        System.out.println("\n--- [3] PLACE ORDER ---");
        if (marketService.getProducts().isEmpty()) {
            System.out.println(">> No products available in the market to purchase.");
            return;
        }

        System.out.println("Current Available Catalog:");
        for (Product p : marketService.getProducts().values()) {
            System.out.println("   " + p);
        }

        String custName = ValidationUtil.readNonEmptyString(scanner, "Enter Customer Name: ");
        String custPhone = ValidationUtil.readPhoneNumber(scanner, "Enter Customer Phone (10 digits): ");

        Map<String, Integer> cart = new LinkedHashMap<>();
        boolean addingItems = true;

        while (addingItems) {
            String prodId = ValidationUtil.readNonEmptyString(scanner, "Enter Product ID to purchase: ");
            int qty = ValidationUtil.readPositiveInt(scanner, "Enter Quantity: ");
            cart.put(prodId, cart.getOrDefault(prodId, 0) + qty);

            System.out.print("Add another product to cart? (yes/no): ");
            String ans = scanner.nextLine().trim();
            if (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("yes")) {
                addingItems = false;
            }
        }

        Order order = marketService.placeOrder(custName, custPhone, cart);
        System.out.println(">> Order Placed Successfully! Order ID: " + order.getOrderId());
    }

    private static void handleRecordPayment() throws ResourceNotFoundException, MarketException {
        System.out.println("\n--- [4] RECORD PAYMENT ---");
        if (marketService.getOrders().isEmpty()) {
            System.out.println(">> No orders available to record payments for.");
            return;
        }

        String orderId = ValidationUtil.readNonEmptyString(scanner, "Enter Order ID: ");
        Order order = marketService.getOrders().get(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID '" + orderId + "' does not exist.");
        }

        if (order.isPaymentSettled()) {
            System.out.println(">> Notice: Payment is already SETTLED for Order " + orderId + ".");
            return;
        }

        double totalDue = order.calculateTotal();
        System.out.printf("Total Payable Amount for Order %s: ₹%.2f\n", orderId, totalDue);
        System.out.println("Select Payment Method:");
        System.out.println("1. Cash");
        System.out.println("2. Debit/Credit Card");
        System.out.println("3. UPI");
        int method = ValidationUtil.readMenuChoice(scanner, 1, 3);

        String paymentId = marketService.generateNextPaymentId();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        Payment payment;

        if (method == 1) {
            double tendered = ValidationUtil.readPositiveDouble(scanner, "Enter Cash Tendered by Customer (₹): ");
            payment = new CashPayment(paymentId, orderId, totalDue, today, tendered);
        } else if (method == 2) {
            String cardNo = ValidationUtil.readNonEmptyString(scanner, "Enter 16-digit Card Number: ");
            String cardHolder = ValidationUtil.readNonEmptyString(scanner, "Enter Cardholder Name: ");
            String txnRef = "TXN-" + System.currentTimeMillis();
            payment = new CardPayment(paymentId, orderId, totalDue, today, cardNo, cardHolder, txnRef);
        } else {
            String upiId = ValidationUtil.readNonEmptyString(scanner, "Enter UPI ID (e.g. user@okhdfcbank): ");
            String refNo = "UPI-" + System.currentTimeMillis();
            payment = new UPIPayment(paymentId, orderId, totalDue, today, upiId, refNo);
        }

        Payment recordedPayment = marketService.recordPayment(orderId, payment);
        System.out.println("\n>> Payment Status: " + recordedPayment.getPaymentStatus());
        System.out.println(">> Payment Receipt: " + recordedPayment);
    }

    private static void handleScheduleDelivery() throws ResourceNotFoundException, PaymentPendingException {
        System.out.println("\n--- [5] SCHEDULE DELIVERY ---");
        String orderId = ValidationUtil.readNonEmptyString(scanner, "Enter Order ID: ");
        Order order = marketService.getOrders().get(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID '" + orderId + "' does not exist.");
        }

        String address = ValidationUtil.readNonEmptyString(scanner, "Enter Delivery Street Address: ");
        String phone = ValidationUtil.readPhoneNumber(scanner, "Enter Recipient Contact Phone: ");
        String date = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

        Delivery delivery = marketService.scheduleDelivery(orderId, address, phone, date);
        System.out.println("\n>> Success! Delivery successfully scheduled.");
        System.out.println(">> Delivery Details: " + delivery);
    }

    private static void handleRequestReturn() throws ResourceNotFoundException, InvalidReturnException {
        System.out.println("\n--- [6] REQUEST RETURN ---");
        String orderId = ValidationUtil.readNonEmptyString(scanner, "Enter Order ID: ");
        Order order = marketService.getOrders().get(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID '" + orderId + "' does not exist.");
        }

        System.out.println("Items in this order:");
        for (OrderItem itm : order.getItems()) {
            System.out.printf("   - Product ID: %s | Name: %s | Purchased Qty: %d\n",
                    itm.getProduct().getProductId(), itm.getProduct().getName(), itm.getQuantity());
        }

        String productId = ValidationUtil.readNonEmptyString(scanner, "Enter Product ID to return: ");
        int returnQty = ValidationUtil.readPositiveInt(scanner, "Enter Return Quantity: ");
        String reason = ValidationUtil.readNonEmptyString(scanner, "Enter Reason for Return: ");

        ReturnRequest returnReq = marketService.requestReturn(orderId, productId, returnQty, reason);
        System.out.println("\n>> Return Request Processed!");
        System.out.println(">> Return ID: " + returnReq.getReturnId());
        System.out.println(">> Decision: " + returnReq.getStatus());
        System.out.println(">> Remarks: " + returnReq.getDecisionRemarks());
        if ("APPROVED".equalsIgnoreCase(returnReq.getStatus())) {
            System.out.printf(">> Refund Processed: ₹%.2f\n", returnReq.getRefundAmount());
            System.out.println(">> Notice: Vendor stock updated accordingly.");
        }
    }
}
