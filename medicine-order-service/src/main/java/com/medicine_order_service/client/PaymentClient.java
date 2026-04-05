package com.medicine_order_service.client;

import com.medicine_order_service.config.FeignConfig;
import com.medicine_order_service.dto.PaymentRequestDto;
import com.medicine_order_service.dto.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE",
        configuration = FeignConfig.class
        )
public interface PaymentClient {
    @PostMapping("/product/v1/checkout/order")
    PaymentResponseDto createPayment(
            @RequestBody PaymentRequestDto request);
}
