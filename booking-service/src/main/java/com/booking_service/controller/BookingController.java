package com.booking_service.controller;

import com.booking_service.dto.BookingRequestDto;
import com.booking_service.dto.BookingResponseDto;
import com.booking_service.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //
    // ✅ Only patients can create bookings
    @PostMapping("/create")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<BookingResponseDto> createBooking(
            @RequestBody BookingRequestDto request,
            Authentication authentication
    ) {
        // patient ID or email from JWT
        String patientEmail = authentication.getName();

        // Override patientId in request to ensure security
        //request.setPatientId(Long.parseLong(patientId));

        BookingResponseDto response = bookingService.createBooking(request,patientEmail);
        return ResponseEntity.ok(response);
    }

    // ✅ Confirm booking (patient-only)
    @PutMapping("/confirm")
    //@PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> confirmBooking(
            @RequestParam Long bookingId,
            Authentication authentication
    ) {
        bookingService.confirmBooking(bookingId);
        return ResponseEntity.ok().build();
    }

    // ✅ Get booking by ID (patient can only view own booking)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDto> getBooking(
            @PathVariable("id") Long bookingId,
            Authentication authentication
    ) {
        String patientId = authentication.getName();
        BookingResponseDto response = bookingService.getBookingById(bookingId);

        // Optional: verify patient owns booking
//        if (!response.getPatientId().equals(Long.parseLong(patientId))) {
//            return ResponseEntity.status(403).build(); // Forbidden
//        }

        return ResponseEntity.ok(response);
    }

    // --------------------------
    // Patient: get all bookings for logged-in patient
    // --------------------------
    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings(
            Authentication authentication
    ) {
        String patientEmail = authentication.getName(); // from JWT
        List<BookingResponseDto> bookings = bookingService.getBookingsByPatientEmail(patientEmail);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/doctor/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<BookingResponseDto>> getMyDoctorBookings(Authentication authentication) {
        String doctorEmail = authentication.getName(); // from JWT
        List<BookingResponseDto> bookings = bookingService.getFutureBookingsByDoctorEmail(doctorEmail);
        return ResponseEntity.ok(bookings);
    }


}
