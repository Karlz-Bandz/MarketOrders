package com.izzisoft.orders.dto;

public record OrderRequest(
        Long productId,
        int quantity
) {
}
