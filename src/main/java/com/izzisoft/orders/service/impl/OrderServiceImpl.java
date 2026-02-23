package com.izzisoft.orders.service.impl;

import com.izzisoft.orders.dto.OrderRequest;
import com.izzisoft.orders.dto.OrderResponse;
import com.izzisoft.orders.dto.ProductResponse;
import com.izzisoft.orders.exception.NotOrderOwnerException;
import com.izzisoft.orders.exception.OrderNotFoundException;
import com.izzisoft.orders.model.MarketOrder;
import com.izzisoft.orders.model.OrderStatus;
import com.izzisoft.orders.repo.MarketOrderRepo;
import com.izzisoft.orders.service.OrderService;
import com.izzisoft.orders.webclient.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final MarketOrderRepo marketOrderRepo;

    private final ProductClient productClient;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {

        ProductResponse productResponse = productClient.getProductById(orderRequest.productId());

        if (productResponse == null) {
            throw new RuntimeException("Product not exists");
        }

        if (productResponse.quantity() < orderRequest.quantity()) {
            throw new RuntimeException("Too small quantity!");
        }

        //Calculate money value and send payment request to the paypal service
        BigDecimal allProductsValue = productResponse.price().multiply(BigDecimal.valueOf(orderRequest.quantity()));
        log.info("Payment sum = {}", allProductsValue.toString());

        productClient.decreaseProductQuantity(productResponse.id(), orderRequest.quantity());

        MarketOrder marketOrder = MarketOrder.builder()
                .productId(productResponse.id())
                .userEmail(orderRequest.userEmail())
                .status(OrderStatus.CREATED)
                .quantity(productResponse.quantity())
                .price(allProductsValue)
                .createdAt(Date.from(Instant.now()))
                .updatedAt(Date.from(Instant.now()))
                .build();

        MarketOrder createdOrder = marketOrderRepo.save(marketOrder);

        return new OrderResponse(
                createdOrder.getId(),
                createdOrder.getUserEmail(),
                createdOrder.getStatus(),
                createdOrder.getPrice()
        );
    }

    @Override
    public OrderResponse getOrderById(Long orderId, String userEmail) {

        MarketOrder foundOrder = marketOrderRepo.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found in datbase")
        );

        if (!foundOrder.getUserEmail().equals(userEmail)) {
            throw new NotOrderOwnerException("You are not owner of the order!");
        }

        return new OrderResponse(
                foundOrder.getId(),
                foundOrder.getUserEmail(),
                foundOrder.getStatus(),
                foundOrder.getPrice()
        );
    }

    @Override
    public List<OrderResponse> getOrdersByUser(String userEmail) {
        return marketOrderRepo.findAllMarketOrdersByUserEmail(userEmail).stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getUserEmail(),
                        order.getStatus(),
                        order.getPrice()
                ))
                .toList();
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return marketOrderRepo.findAll().stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getUserEmail(),
                        order.getStatus(),
                        order.getPrice()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {

        MarketOrder foundOrder = marketOrderRepo.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found!")
        );

        foundOrder.setStatus(orderStatus);
    }
}
