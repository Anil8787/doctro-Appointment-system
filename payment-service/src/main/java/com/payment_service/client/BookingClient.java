package com.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "BOOKING-SERVICE")
public interface BookingClient {
    @PutMapping("/api/v1/booking/confirm")
    public void confirmBooking(@RequestParam("bookingId") Long bookingId);
}
