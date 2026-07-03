package com.izzisoft.kafka;

import com.izzisoft.orders.model.PaymentStatus;

public record OrderStatusEvent(
        Long orderId,
        PaymentStatus paymentStatus
) {
}
