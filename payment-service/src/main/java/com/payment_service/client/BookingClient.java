package com.payment_service.client;

import com.payment_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "BOOKING-SERVICE",configuration = FeignConfig.class)
public interface BookingClient {
    @PutMapping("/api/v1/booking/confirm")
    public void confirmBooking(@RequestParam("bookingId") Long bookingId);
}
