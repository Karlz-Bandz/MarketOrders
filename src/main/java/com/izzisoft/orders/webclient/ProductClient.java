package com.izzisoft.orders.webclient;

import com.izzisoft.orders.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final WebClient webClient;

    public void decreaseProductQuantity(Long productId, int decreaseValue) {
       webClient.put()
               .uri("/products/decrease/{id}/{decreaseValue}", productId, decreaseValue)
               .retrieve()
               .toBodilessEntity()
               .block();
    }

    public ProductResponse getProductById(Long productId) {
        return webClient.get()
                .uri("/products/{id}", productId)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();
    }
}
