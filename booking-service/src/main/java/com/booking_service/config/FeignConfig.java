package com.booking_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor internalServiceInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Service-Token", "booking-service-secret");
        };
    }
}