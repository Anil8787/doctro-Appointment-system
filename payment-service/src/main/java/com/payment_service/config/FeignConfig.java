package com.payment_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor paymentServiceInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Service-Token", "payment-service-secret");
        };
    }
}
