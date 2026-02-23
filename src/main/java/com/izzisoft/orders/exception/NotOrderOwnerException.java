package com.izzisoft.orders.exception;

public class NotOrderOwnerException extends RuntimeException {
    public NotOrderOwnerException(String message) {
        super(message);
    }
}
