package com.izzisoft.orders.webclient;

import com.izzisoft.orders.dto.ProductResponse;
import com.izzisoft.orders.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final WebClient productWebClient;

    private final JwtService jwtService;

    public void increaseProductQuantity(Long productId, int increaseValue) {
        String serviceToken = jwtService.generateServiceToken();

        productWebClient.put()
                .uri("/products/increase/{id}/{increaseValue}", productId, increaseValue)
                .header("Authorization", "Bearer " + serviceToken)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void decreaseProductQuantity(Long productId, int decreaseValue) {
      String serviceToken = jwtService.generateServiceToken();

      productWebClient.put()
               .uri("/products/decrease/{id}/{decreaseValue}", productId, decreaseValue)
               .header("Authorization", "Bearer " + serviceToken)
               .retrieve()
               .toBodilessEntity()
               .block();
    }

    public ProductResponse getProductById(Long productId) {
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String userToken = auth.getToken().getTokenValue();

        return productWebClient.get()
                .uri("/products/{id}", productId)
                .header("Authorization", "Bearer " + userToken)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();
    }
}
