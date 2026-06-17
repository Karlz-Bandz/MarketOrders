package com.izzisoft.orders.webclient;

import com.izzisoft.orders.dto.PaymentRequest;
import com.izzisoft.orders.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(@Value("${url.payment}") String url) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .build();
    }

    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/payment")
                .body(paymentRequest)
                .retrieve()
                .body(PaymentResponse.class);
    }
}
