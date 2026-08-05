package com.izzisoft.orders.webclient;

import com.izzisoft.orders.dto.PaymentRequest;
import com.izzisoft.orders.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestTemplate restTemplate;

    private static final String PAYMENT_SERVICE_URL = "http://localhost:8083";

    public PaymentResponse processPayment(PaymentRequest paymentRequest) {

        String url = PAYMENT_SERVICE_URL + "/payment";

        return restTemplate.postForObject(
                url,
                paymentRequest,
                PaymentResponse.class
        );
    }
}
