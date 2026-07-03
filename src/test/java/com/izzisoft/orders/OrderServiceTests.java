package com.izzisoft.orders;

import com.izzisoft.kafka.PaymentEvent;
import com.izzisoft.orders.dto.OrderRequest;
import com.izzisoft.orders.dto.OrderResponse;
import com.izzisoft.orders.dto.ProductResponse;
import com.izzisoft.orders.exception.NotOrderOwnerException;
import com.izzisoft.orders.exception.OrderNotFoundException;
import com.izzisoft.orders.exception.ProductNotExistsException;
import com.izzisoft.orders.exception.TooSmallProductQuantityException;
import com.izzisoft.orders.model.MarketOrder;
import com.izzisoft.orders.model.PaymentStatus;
import com.izzisoft.orders.repo.MarketOrderRepo;
import com.izzisoft.orders.service.impl.OrderServiceImpl;
import com.izzisoft.orders.webclient.ProductClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private MarketOrderRepo marketOrderRepo;

    @Mock
    private ProductClient productClient;

    @Mock
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private ProductResponse productResponse;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        productResponse = new ProductResponse(
                1L,
                "Laptop",
                "Desc",
                10,
                BigDecimal.valueOf(1000)
        );

        orderRequest = new OrderRequest(
                1L,
                2
        );
    }

    @Test
    void getOrderByIdNotOwnerException() {
        MarketOrder marketOrder = MarketOrder.builder()
                .userEmail("test@test.com")
                .price(BigDecimal.valueOf(12))
                .build();

        when(marketOrderRepo.findById(1L)).thenReturn(Optional.of(marketOrder));

        assertThrows(NotOrderOwnerException.class, () -> {
            orderService.getOrderById(1L, "kar@test.com");
        });
    }

    @Test
    void getOrderByIdOrderNotFound() {
        when(marketOrderRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(1L, "test@test.com");
        });
    }

    @Test
    void getOrderById() {
        MarketOrder marketOrder = MarketOrder.builder()
                .userEmail("test@test.com")
                .price(BigDecimal.valueOf(12))
                .build();

        when(marketOrderRepo.findById(1L)).thenReturn(Optional.of(marketOrder));

        OrderResponse serviceResponse = orderService.getOrderById(1L, "test@test.com");

        assertEquals(12, serviceResponse.price().intValue());
        assertEquals("test@test.com", serviceResponse.ownerEmail());
    }

    @Test
    void tooSmallProductQuantityException() {
        OrderRequest largeQuantityRequest = new OrderRequest(
                1L,
                1000
        );

        when(productClient.getProductById(1L)).thenReturn(productResponse);

        assertThrows(TooSmallProductQuantityException.class, () -> {
            orderService.createOrder(largeQuantityRequest, "test@test.com");
        });
    }

    @Test
    void productNotExistsException() {
        when(productClient.getProductById(1L)).thenReturn(null);

        assertThrows(ProductNotExistsException.class, () -> {
            orderService.createOrder(orderRequest, "test@test.com");
        });
    }

    @Test
    void shouldCreateOrderSuccessfully() {

        MarketOrder savedOrder = MarketOrder.builder()
                .id(1L)
                .productId(1L)
                .userEmail("test@test.com")
                .status(PaymentStatus.CREATED)
                .price(BigDecimal.valueOf(2000))
                .quantity(2)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        when(productClient.getProductById(1L)).thenReturn(productResponse);
        when(marketOrderRepo.save(any())).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(orderRequest, "test@test.com");

        assertNotNull(response);
        assertEquals(PaymentStatus.CREATED, response.status());
        assertEquals(BigDecimal.valueOf(2000), response.price());

        verify(productClient).decreaseProductQuantity(1L, 2);
        verify(kafkaTemplate).send(eq("payment"), any(PaymentEvent.class));
        verify(productClient, never()).increaseProductQuantity(anyLong(), anyInt());
    }
}
