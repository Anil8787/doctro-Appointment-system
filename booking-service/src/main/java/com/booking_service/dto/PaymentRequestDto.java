package com.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class PaymentRequestDto {
    private Long bookingId;
    private String name;
    private BigDecimal amount;
    private Long quantity;
    private String currency;
}
