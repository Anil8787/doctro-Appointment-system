package com.payment_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    // reference from booking-service
    private Long bookingId;

    // payment details
    private BigDecimal amount;     // 299.00
    private String currency;       // USD
    private Long quantity;         // 1
    private String name;           // Doctor Appointment with Dr. X
}
