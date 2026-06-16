package com.izzisoft.orders.exception;

public class TooSmallProductQuantityException extends RuntimeException {
    public TooSmallProductQuantityException(String message) {
        super(message);
    }
}
