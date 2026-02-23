package com.izzisoft.orders.dto;

public record OrderRequest(
        Long productId,
        String userEmail,
        int quantity
) {
}
