package com.example.nlayered.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * HTTP client configuration.
 * Provides named RestClient beans for each downstream service.
 * Swap in real HTTP clients (e.g., actual Feign once Spring Cloud
 * supports Spring Boot 4.x) without touching service code.
 */
@Configuration
public class OpenFeignConfig {

    @Bean("notificationRestClient")
    public RestClient notificationRestClient(
            @Value("${services.notification.url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean("inventoryRestClient")
    public RestClient inventoryRestClient(
            @Value("${services.inventory.url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}