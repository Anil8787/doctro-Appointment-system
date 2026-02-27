package com.booking_service.service;

import com.booking_service.client.DoctorClient;
import com.booking_service.client.PatientClient;
import com.booking_service.client.PaymentClient;
import com.booking_service.dto.*;
import com.booking_service.entity.Booking;
import com.booking_service.enums.BookingStatus;
import com.booking_service.repository.BookingRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    private final DoctorClient doctorClient;
    private final PatientClient patientClient;
    private final PaymentClient paymentClient;
    private final BookingRepository bookingRepository;

    public BookingService(
            DoctorClient doctorClient,
            PatientClient patientClient,
            BookingRepository bookingRepository,
            PaymentClient paymentClient
            ) {

        this.doctorClient = doctorClient;
        this.patientClient = patientClient;
        this.bookingRepository = bookingRepository;
        this.paymentClient = paymentClient;
    }

    public BookingResponseDto createBooking(BookingRequestDto request) {

//        // Get current authentication
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//
//        String token = (String) auth.getCredentials(); // JWT token
//        System.out.println("token:"+token);
//        // 1️⃣ Validate doctor & patient
        Doctor doctor = doctorClient.getDoctorById(request.getDoctorId());
        Patient patient = patientClient.getPatientById(request.getPatientId());

        // 2️⃣ Validate slot availability
        boolean slotAvailable = false;

        for (DoctorAppointmentSchedule schedule : doctor.getAppointmentSchedules()) {
            if (schedule.getDate().isEqual(request.getDate())) {
                for (TimeSlots slot : schedule.getTimeSlots()) {
                    if (slot.getTime().equals(request.getTime()) && slot.isAvailable()) {
                        slotAvailable = true;
                        break;
                    }
                }
            }
        }

        if (!slotAvailable) {
            throw new RuntimeException("Selected slot not available");
        }

        // ✅ Check if the slot is already booked
        boolean alreadyBooked = bookingRepository.existsByDoctorIdAndDateAndTimeAndStatus(
                request.getDoctorId(),
                request.getDate(),
                request.getTime(),
                BookingStatus.CONFIRMED
        );

        if (alreadyBooked) {
            throw new RuntimeException("Selected slot is already booked");
        }

        // 3️⃣ Create booking (PENDING_PAYMENT)
        Booking booking = new Booking();
        booking.setDoctorId(request.getDoctorId());
        booking.setPatientId(request.getPatientId());
        booking.setDate(request.getDate());
        booking.setTime(request.getTime());
        booking.setAmount(request.getAmount());
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setCreatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        // 5️⃣ Prepare payment request
        PaymentRequestDto paymentRequest = new PaymentRequestDto();
        paymentRequest.setBookingId(savedBooking.getId());
        paymentRequest.setName("Doctor Appointment with " + doctor.getName());
        paymentRequest.setAmount(request.getAmount()); // make sure BookingRequestDto has amount
        paymentRequest.setQuantity(1L);
        paymentRequest.setCurrency("INR"); // or pass from request

//        PaymentResponseDto paymentResponse =
//                paymentClient.createPayment(paymentRequest);

        PaymentResponseDto paymentResponse =
                callPaymentService(paymentRequest);
        if (paymentResponse == null || "FAILED".equals(paymentResponse.getStatus())) {
            BookingResponseDto response = new BookingResponseDto();
            response.setBookingId(savedBooking.getId());
            response.setStatus(BookingStatus.PENDING_PAYMENT.name());
            response.setMessage("Payment service is temporarily unavailable. Please try again later.");
            return response;
        }

        // store Stripe sessionId
        savedBooking.setPaymentSessionId(paymentResponse.getSessionId());
        savedBooking.setPaymentUrl(paymentResponse.getSessionUrl()); // ✅ store payment URL
        bookingRepository.save(savedBooking);

        // 4️⃣ Prepare response
        BookingResponseDto response = new BookingResponseDto();
        response.setBookingId(savedBooking.getId());
        response.setStatus(savedBooking.getStatus().name());
        response.setMessage("Booking created. Proceed to payment.");
        response.setSessionId(paymentResponse.getSessionId());
        response.setPaymentUrl(paymentResponse.getSessionUrl());
        response.setPatientEmail(patient.getEmail());
        response.setDoctorEmail(doctor.getName());

        return response;
    }


    public void confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        // ✅ idempotent
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return;
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        Doctor doctor = doctorClient.getDoctorById(booking.getDoctorId());

        boolean updated = false;
        for (DoctorAppointmentSchedule schedule : doctor.getAppointmentSchedules()) {
            if (schedule.getDate().isEqual(booking.getDate())) {
                for (TimeSlots slot : schedule.getTimeSlots()) {
                    if (slot.getTime().equals(booking.getTime()) && slot.isAvailable()) {
                        slot.setAvailable(false);  // <-- mark unavailable
                        updated = true;
                        break;
                    }
                }
            }
            if (updated) break;
        }

        // 3️⃣ Save updated schedule back to doctor service
        doctorClient.updateDoctorSchedule(doctor.getId(), doctor.getAppointmentSchedules());
    }

    public BookingResponseDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        // ✅ Fetch patient details from PatientClient
        Patient patient = patientClient.getPatientById(booking.getPatientId());
        Doctor doctor = doctorClient.getDoctorById(booking.getDoctorId());

        BookingResponseDto response = new BookingResponseDto();
        response.setBookingId(booking.getId());
        response.setStatus(booking.getStatus().name());
        response.setPatientEmail(patient.getEmail());
        response.setDoctorEmail(doctor.getName());
        //response.setPaymentUrl(booking.getPaymentUrl());
        response.setMessage("Booking retrieved successfully");
        response.setPaymentUrl(booking.getPaymentUrl()); // optional, if stored
        response.setSessionId(booking.getPaymentSessionId());   // optional, if stored

        return response;
    }


    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public PaymentResponseDto callPaymentService(PaymentRequestDto paymentRequest) {
        return paymentClient.createPayment(paymentRequest);
    }

    public PaymentResponseDto paymentFallback(
            PaymentRequestDto request,
            Exception ex
    ) {
        PaymentResponseDto response = new PaymentResponseDto();
        response.setStatus("FAILED");
        response.setSessionId(null);
        response.setSessionUrl(null);
        return response;
    }
}
