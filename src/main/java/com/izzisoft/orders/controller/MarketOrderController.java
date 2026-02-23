package com.izzisoft.orders.controller;

import com.izzisoft.orders.dto.OrderRequest;
import com.izzisoft.orders.dto.OrderResponse;
import com.izzisoft.orders.model.OrderStatus;
import com.izzisoft.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class MarketOrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        return new ResponseEntity<>(orderService.createOrder(orderRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{userEmail}/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("userEmail") String userEmail, @PathVariable("orderId") Long orderId) {
        return new ResponseEntity<>(orderService.getOrderById(orderId, userEmail), HttpStatus.OK);
    }

    @GetMapping("/{userEmail}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable("userEmail") String userEmail) {
        return new ResponseEntity<>(orderService.getOrdersByUser(userEmail), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrderStatus orderStatus) {
        orderService.updateOrderStatus(orderId, orderStatus);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
