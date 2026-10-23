package com.farmersmarket.exception;

public class InsufficientStockException extends MarketException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
