package com.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponseDto {
    private Long bookingId;
    private String status;
    private String message;   // add this
    private String sessionId;
    private String paymentUrl;
}
