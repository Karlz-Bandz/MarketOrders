package com.izzisoft.orders.dto;

import com.izzisoft.orders.model.PaymentStatus;

import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        String ownerEmail,
        PaymentStatus status,
        BigDecimal price
) {
}
