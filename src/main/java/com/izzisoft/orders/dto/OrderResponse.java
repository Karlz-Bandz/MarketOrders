package com.izzisoft.orders.dto;

import com.izzisoft.orders.model.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        String ownerEmail,
        OrderStatus status,
        BigDecimal price
) {
}
