

## Problem Statement

Design and implement a Java console application for a Farmers' Market system that manages vendors, products, orders, payments, deliveries, and returns. The application should demonstrate object-oriented principles and keep inventory accurate per vendor.

### Class Requirements
`Vendor`, `Product`, `Order`, `OrderItem`, `Payment`, `Delivery`, `ReturnRequest`

### Business Rules
1. Orders can be placed only for available vendor inventory.
2. Stock decreases on order confirmation and increases on approved returns.
3. Payments must be settled before scheduling delivery.
4. Returns are allowed within policy and cannot exceed purchased quantity.
5. Each order item links directly to a vendor's product.

### Menu Options
Add Vendor / Add Product / Place Order / Record Payment / Schedule Delivery / Request Return / Display Inventory / Exit

### Expected Output
- Show order invoice, delivery status, and return decisions.
- Show vendor-wise inventory updates after each event.
- Show customer order history and totals.

---

## Object-Oriented Design Highlights

| OOP Principle | Implementation |
|---|---|
| **Encapsulation** | All entity fields are `private` with getters/setters and validation logic |
| **Inheritance** | Abstract `Payment` class extended by `CashPayment`, `CardPayment`, `UPIPayment` |
| **Polymorphism** | Dynamic dispatch on `processPayment()` and `getPaymentMode()` across payment types |
| **Composition** | `Order` composes multiple `OrderItem` instances (lifecycle dependency) |
| **Aggregation** | `Order` aggregates `Payment`, `Delivery`; `Vendor` aggregates `Product` |
| **Exception Handling** | Custom checked exceptions: `InsufficientStockException`, `PaymentPendingException`, `InvalidReturnException`, `ResourceNotFoundException`, `InvalidInputException` |

---

## Project Structure

```
farmers-market-system/
├── src/
│   └── com/
│       └── farmersmarket/
│           ├── exception/
│           │   ├── MarketException.java
│           │   ├── InsufficientStockException.java
│           │   ├── PaymentPendingException.java
│           │   ├── InvalidReturnException.java
│           │   ├── ResourceNotFoundException.java
│           │   └── InvalidInputException.java
│           ├── model/
│           │   ├── Vendor.java
│           │   ├── Product.java
│           │   ├── OrderItem.java
│           │   ├── Payment.java           (Abstract)
│           │   ├── CashPayment.java       (extends Payment)
│           │   ├── CardPayment.java       (extends Payment)
│           │   ├── UPIPayment.java        (extends Payment)
│           │   ├── Delivery.java
│           │   ├── ReturnRequest.java
│           │   └── Order.java
│           ├── service/
│           │   └── FarmersMarketService.java
│           ├── util/
│           │   └── ValidationUtil.java
│           └── main/
│               └── FarmersMarketApp.java
└── README.md
```

---

## How to Compile and Run

### Prerequisites
- **Java Development Kit (JDK) 17** or higher
- **Eclipse IDE** (recommended) or any terminal / command prompt

### Option A: Using Eclipse IDE

1. Open Eclipse IDE.
2. Go to **File → New → Java Project**, set Project Name to `FarmersMarketSystem`.
3. Copy the `com` folder into the project's `src` directory.
4. Right-click on `FarmersMarketApp.java` → **Run As → Java Application**.

### Option B: Using Command Line

```bash
# Clone the repository
git clone https://github.com/________/farmers-market-system.git
cd farmers-market-system

# Compile all source files
javac -d bin src/com/farmersmarket/*/*.java

# Run the application
java -cp bin com.farmersmarket.main.FarmersMarketApp
```

---

## Menu Options

```
+-------------------------------------------------------------+
|                   FARMERS' MARKET MAIN MENU                 |
+-------------------------------------------------------------+
| 1. Add Vendor                                               |
| 2. Add Product                                              |
| 3. Place Order                                              |
| 4. Record Payment                                           |
| 5. Schedule Delivery                                        |
| 6. Request Return                                           |
| 7. Display Inventory                                        |
| 8. Customer Order History & Invoices                        |
| 9. Exit                                                     |
+-------------------------------------------------------------+
```

---

## Sample Execution Scenarios

| Scenario | Demonstrates |
|---|---|
| Place order with insufficient stock | Business Rule 1 + `InsufficientStockException` |
| Place valid multi-vendor order | Invoice generation, stock deduction (Rule 2), vendor linking (Rule 5) |
| Schedule delivery before payment | Business Rule 3 + `PaymentPendingException` |
| Settle payment via UPI/Cash/Card | Polymorphism on `Payment.processPayment()` |
| Return exceeding purchased quantity | Business Rule 4 + `InvalidReturnException` |
| Approved return for damaged goods | Stock restoration (Rule 2) + refund calculation |
| View customer order history | Full order trail with items, payments, deliveries, returns |

---

## Technologies Used

- **Language:** Java 17+
- **IDE:** Eclipse IDE 2024
- **Build:** Manual compilation (no build tool required)
- **Architecture:** Layered – Model / Service / Util / Exception / Main
