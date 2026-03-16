package com.izzisoft.orders.controller;

import com.izzisoft.orders.dto.OrderRequest;
import com.izzisoft.orders.dto.OrderResponse;
import com.izzisoft.orders.model.OrderStatus;
import com.izzisoft.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class MarketOrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest, Principal principal) {
        return new ResponseEntity<>(orderService.createOrder(orderRequest, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(Principal principal, @PathVariable("orderId") Long orderId) {
        return new ResponseEntity<>(orderService.getOrderById(orderId, principal.getName()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(Principal principal) {
        return new ResponseEntity<>(orderService.getOrdersByUser(principal.getName()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrderStatus orderStatus) {
        orderService.updateOrderStatus(orderId, orderStatus);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
