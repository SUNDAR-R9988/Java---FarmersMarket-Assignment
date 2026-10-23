package com.farmersmarket.model;

public class CardPayment extends Payment {
    private String cardNumberMasked;
    private String cardHolderName;
    private String transactionRef;

    public CardPayment(String paymentId, String orderId, double amount, String paymentDate,
                       String cardNumber, String cardHolderName, String transactionRef) {
        super(paymentId, orderId, amount, paymentDate);
        this.cardHolderName = cardHolderName;
        this.transactionRef = transactionRef;
        if (cardNumber != null && cardNumber.length() >= 4) {
            this.cardNumberMasked = "XXXX-XXXX-XXXX-" + cardNumber.substring(cardNumber.length() - 4);
        } else {
            this.cardNumberMasked = "XXXX-XXXX-XXXX-0000";
        }
    }

    public String getCardNumberMasked() {
        return cardNumberMasked;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    @Override
    public boolean processPayment() {
        // Simulates card authorization
        setPaymentStatus("SETTLED");
        return true;
    }

    @Override
    public String getPaymentMode() {
        return "DEBIT/CREDIT CARD";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Card: %s | Holder: %s | Txn: %s",
                cardNumberMasked, cardHolderName, transactionRef);
    }
}
