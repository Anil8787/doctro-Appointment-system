package com.booking_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDto {
    private Long bookingId;
    private String status;
    private String message;   // add this
    private String sessionId;
    private String paymentUrl;
    private String patientEmail;
    private String doctorEmail;
    private LocalDate date;
    private LocalTime time;
}
