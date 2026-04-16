package com.izzisoft.orders.dto;

import com.izzisoft.orders.model.OrderStatus;

public record StatusRequest(OrderStatus status) {
}
