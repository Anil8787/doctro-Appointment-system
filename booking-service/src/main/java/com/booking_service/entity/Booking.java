package com.booking_service.entity;

import com.booking_service.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name="bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long doctorId;
    private long patientId;

    private LocalDate date;
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String paymentSessionId;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_url",length = 500)
    private String paymentUrl;
}

