package com.booking_service.controller;

import com.booking_service.dto.BookingRequestDto;
import com.booking_service.dto.BookingResponseDto;
import com.booking_service.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingService bookingService;
    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // POST instead of GET ✅
    //http://localhost:8083/api/v1/booking/create
    @PostMapping("/create")
    public BookingResponseDto createBooking(
            @RequestBody BookingRequestDto request) {

        return bookingService.createBooking(request);
    }

    @PutMapping("/confirm")
    public void confirmBooking(@RequestParam String sessionId) {
        bookingService.confirmBooking(sessionId);
    }

    // GET booking by ID
    //http://localhost:8083/api/v1/booking/2
    @GetMapping("/{id}")
    public BookingResponseDto getBooking(@PathVariable("id") Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

}
