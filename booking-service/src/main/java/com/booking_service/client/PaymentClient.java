package com.booking_service.client;

import com.booking_service.dto.PaymentRequestDto;
import com.booking_service.dto.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE",
        configuration = com.booking_service.config.FeignConfig.class
        )
public interface PaymentClient {
    @PostMapping("/product/v1/checkout")
    PaymentResponseDto createPayment(
            @RequestBody PaymentRequestDto request);
}
