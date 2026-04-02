package com.izzisoft.orders.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    @Bean
    public WebClient paymentWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
    }
}
