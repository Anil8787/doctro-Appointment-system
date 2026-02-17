package com.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BookingRequestDto {
    private Long doctorId;
    private Long patientId;
    private LocalDate date;
    private LocalTime time;
    private BigDecimal amount; // add this to send payment amount
}
