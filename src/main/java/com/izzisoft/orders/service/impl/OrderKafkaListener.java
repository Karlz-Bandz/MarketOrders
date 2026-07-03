package com.izzisoft.orders.service.impl;

import com.izzisoft.kafka.OrderStatusEvent;
import com.izzisoft.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderKafkaListener {

    private final OrderService orderService;

    @KafkaListener(topics = "order-payment-status", groupId = "payment-group")
    public void getPaymentStatus(OrderStatusEvent orderStatusEvent) {
        orderService.updateOrderStatus(orderStatusEvent.orderId(), orderStatusEvent.paymentStatus());
    }
}
