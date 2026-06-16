package com.izzisoft.orders.service.impl;

import com.izzisoft.orders.dto.OrderRequest;
import com.izzisoft.orders.dto.OrderResponse;
import com.izzisoft.orders.dto.PaymentRequest;
import com.izzisoft.orders.dto.PaymentResponse;
import com.izzisoft.orders.dto.ProductResponse;
import com.izzisoft.orders.exception.NotOrderOwnerException;
import com.izzisoft.orders.exception.OrderNotFoundException;
import com.izzisoft.orders.exception.ProductNotExistsException;
import com.izzisoft.orders.exception.TooSmallProductQuantityException;
import com.izzisoft.orders.model.MarketOrder;
import com.izzisoft.orders.model.OrderStatus;
import com.izzisoft.orders.repo.MarketOrderRepo;
import com.izzisoft.orders.service.OrderService;
import com.izzisoft.orders.webclient.PaymentClient;
import com.izzisoft.orders.webclient.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final MarketOrderRepo marketOrderRepo;

    private final ProductClient productClient;

    private final PaymentClient paymentClient;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest, String userEmail) {
        ProductResponse productResponse = productClient.getProductById(orderRequest.productId());
        validateProduct(productResponse, orderRequest);
        BigDecimal allProductsValue = calculateAllProductsValue(productResponse, orderRequest);
        productClient.decreaseProductQuantity(productResponse.id(), orderRequest.quantity());

        MarketOrder marketOrder = MarketOrder.builder()
                .productId(productResponse.id())
                .userEmail(userEmail)
                .status(OrderStatus.CREATED)
                .quantity(orderRequest.quantity())
                .price(allProductsValue)
                .createdAt(Date.from(Instant.now()))
                .updatedAt(Date.from(Instant.now()))
                .build();

        MarketOrder createdOrder = marketOrderRepo.save(marketOrder);

        PaymentRequest paymentRequest = new PaymentRequest(
                createdOrder.getId(),
                allProductsValue,
                "CARD"
        );

        PaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);

        updateOrderStatus(createdOrder, paymentResponse);

        if (!"SUCCESS".equals(paymentResponse.status())) {
            productClient.increaseProductQuantity(productResponse.id(), orderRequest.quantity());
        }

        return new OrderResponse(
                createdOrder.getId(),
                createdOrder.getUserEmail(),
                createdOrder.getStatus(),
                createdOrder.getPrice()
        );
    }

    @Override
    public OrderResponse getOrderById(Long orderId, String userEmail) {

        MarketOrder foundOrder = getMarketOrderById(orderId);

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
        MarketOrder foundOrder = getMarketOrderById(orderId);
        foundOrder.setStatus(orderStatus);
    }

    private MarketOrder getMarketOrderById(Long orderId) {
        return marketOrderRepo.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found!")
        );
    }

    private void updateOrderStatus(MarketOrder createdOrder, PaymentResponse paymentResponse) {
        if (paymentResponse.status().equals("SUCCESS")) {
            createdOrder.setStatus(OrderStatus.PAID);
        } else {
            createdOrder.setStatus(OrderStatus.CANCELED);
        }
    }

    private BigDecimal calculateAllProductsValue(ProductResponse productResponse, OrderRequest orderRequest) {
        return productResponse.price().multiply(BigDecimal.valueOf(orderRequest.quantity()));
    }

    private void validateProduct(ProductResponse productResponse, OrderRequest orderRequest) {
        if (productResponse == null) {
            throw new ProductNotExistsException("Product not exists!");
        }

        if (productResponse.quantity() < orderRequest.quantity()) {
            throw new TooSmallProductQuantityException("Too small quantity!");
        }
    }
}
