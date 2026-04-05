package com.booking_service.repository;

import com.booking_service.entity.Booking;
import com.booking_service.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    boolean existsByDoctorIdAndDateAndTimeAndStatus(Long doctorId, LocalDate date, LocalTime time, BookingStatus bookingStatus);

    // ✅ Find all bookings by patient ID
    List<Booking> findByPatientId(Long patientId);
    //Optional<Booking> findByPaymentBookingId(Long bookingId);

    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime time);

    boolean existsByDoctorIdAndDateAndTimeAndStatusIn(Long doctorId, LocalDate date, LocalTime time, List<BookingStatus> pendingPayment);

    // ✅ Corrected method for future bookings for a doctor
    List<Booking> findByDoctorIdAndDateGreaterThanEqual(Long doctorId, LocalDate date);
}
