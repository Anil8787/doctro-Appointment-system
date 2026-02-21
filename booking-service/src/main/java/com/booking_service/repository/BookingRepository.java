package com.booking_service.repository;

import com.booking_service.entity.Booking;
import com.booking_service.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    boolean existsByDoctorIdAndDateAndTimeAndStatus(Long doctorId, LocalDate date, LocalTime time, BookingStatus bookingStatus);
    //Optional<Booking> findByPaymentBookingId(Long bookingId);
}
