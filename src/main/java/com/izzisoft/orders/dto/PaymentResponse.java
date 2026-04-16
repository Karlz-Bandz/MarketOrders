package com.izzisoft.orders.dto;

public record PaymentResponse(
        Long orderId,
        String status
) {
}
