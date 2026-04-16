package com.izzisoft.orders.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        int quantity,
        BigDecimal price
) {
}
