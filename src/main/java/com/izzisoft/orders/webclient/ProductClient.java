package com.izzisoft.orders.webclient;

import com.izzisoft.orders.dto.ProductResponse;
import com.izzisoft.orders.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

    private final RestClient restClient;

    private final JwtService jwtService;

    public ProductClient(@Value("${url.product}") String url, JwtService jwtService) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .build();
        this.jwtService = jwtService;
    }

    public void increaseProductQuantity(Long productId, int increaseValue) {
        String serviceToken = jwtService.generateServiceToken();
        String url = "/products/increase/{id}/{increaseValue}";

        restClient.put()
                .uri(url, productId, increaseValue)
                .header("Authorization", "Bearer " + serviceToken)
                .retrieve()
                .toBodilessEntity();
    }

    public void decreaseProductQuantity(Long productId, int decreaseValue) {
        String serviceToken = jwtService.generateServiceToken();
        String url = "/products/decrease/{id}/{decreaseValue}";

        restClient.put()
                .uri(url, productId, decreaseValue)
                .header("Authorization", "Bearer " + serviceToken)
                .retrieve()
                .toBodilessEntity();
    }

    public ProductResponse getProductById(Long productId) {
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String userToken = auth.getToken().getTokenValue();
        String url =  "/products/{id}";

        return restClient.get()
                .uri(url, productId)
                .header("Authorization", "Bearer " + userToken)
                .retrieve()
                .body(ProductResponse.class);
    }
}
