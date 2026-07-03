package com.izzisoft.orders.service;

import com.izzisoft.orders.dto.OrderRequest;
import com.izzisoft.orders.dto.OrderResponse;
import com.izzisoft.orders.model.PaymentStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest, String userEmail);
    OrderResponse getOrderById(Long orderId, String userEmail);
    List<OrderResponse> getOrdersByUser(String userEmail);
    List<OrderResponse> getAllOrders();
    void updateOrderStatus(Long orderId, PaymentStatus orderStatus);
}
