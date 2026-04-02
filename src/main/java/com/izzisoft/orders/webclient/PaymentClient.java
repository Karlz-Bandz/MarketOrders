package com.izzisoft.orders.webclient;

import com.izzisoft.orders.dto.PaymentRequest;
import com.izzisoft.orders.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final WebClient paymentWebClient;

    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        return paymentWebClient.post()
                .uri("/payment")
                .bodyValue(paymentRequest)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }
}
