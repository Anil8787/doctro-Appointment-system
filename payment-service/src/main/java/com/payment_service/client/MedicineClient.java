package com.payment_service.client;

import com.payment_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "MEDICINE-ORDER-SERVICE",configuration = FeignConfig.class)
public interface MedicineClient {
    @PostMapping("/api/v1/orders/payment-success")
    void paymentSuccess(@RequestParam Long orderId);
}
