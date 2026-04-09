package com.example.nlayered.common.config;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OpenFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options feignRequestOptions() {
        return new Request.Options(5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS, true);
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) -> {
            String msg = "Feign call failed for [%s] — HTTP %d".formatted(methodKey, response.status());
            if (response.status() == 404) {
                return new IllegalArgumentException(msg);
            }
            return new RuntimeException(msg);
        };
    }
}