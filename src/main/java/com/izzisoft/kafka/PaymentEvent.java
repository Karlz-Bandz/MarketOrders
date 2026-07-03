package com.izzisoft.kafka;

import java.math.BigDecimal;

public record PaymentEvent(
        Long orderId,
        Long productId,
        int productsAmount,
        BigDecimal amount,
        String paymentMethod
) {
}
