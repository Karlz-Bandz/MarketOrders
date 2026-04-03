package com.izzisoft.orders.webclient;

import com.izzisoft.orders.dto.ProductResponse;
import com.izzisoft.orders.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private static final String PRODUCT_SERVICE_URL = "http://localhost:8080";

    private final RestTemplate restTemplate;

    private final JwtService jwtService;

    public void increaseProductQuantity(Long productId, int increaseValue) {

        String serviceToken = jwtService.generateServiceToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        String url = PRODUCT_SERVICE_URL + "/products/increase/{id}/{increaseValue}";

        restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                Void.class,
                productId,
                increaseValue
        );
    }

    public void decreaseProductQuantity(Long productId, int decreaseValue) {

        String serviceToken = jwtService.generateServiceToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        String url = PRODUCT_SERVICE_URL + "/products/decrease/{id}/{decreaseValue}";

        restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                Void.class,
                productId,
                decreaseValue
        );
    }

    public ProductResponse getProductById(Long productId) {
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String userToken = auth.getToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + userToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        String url = PRODUCT_SERVICE_URL + "/products/{id}";

        ResponseEntity<ProductResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                ProductResponse.class,
                productId

        );

        return response.getBody();
    }
}
