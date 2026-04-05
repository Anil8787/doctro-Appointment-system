package com.booking_service.service;

import com.booking_service.client.DoctorClient;
import com.booking_service.dto.Doctor;
import com.booking_service.dto.DoctorAppointmentSchedule;
import com.booking_service.dto.TimeSlots;
import com.booking_service.entity.Booking;
import com.booking_service.enums.BookingStatus;
import com.booking_service.repository.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingScheduler {

    private final BookingRepository bookingRepository;
    private final DoctorClient doctorClient;

    public BookingScheduler(
            BookingRepository bookingRepository,
            DoctorClient doctorClient
    ) {
        this.bookingRepository = bookingRepository;
        this.doctorClient = doctorClient;
    }

    // 🔁 Runs every 1 minute
    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void cancelStalePendingBookings() {

        // ⏱ Cancel if payment not done within 5 minutes
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);

        List<Booking> staleBookings =
                bookingRepository.findByStatusAndCreatedAtBefore(
                        BookingStatus.PENDING_PAYMENT,
                        cutoff
                );

        if (staleBookings.isEmpty()) return;

        for (Booking booking : staleBookings) {

            // 1️⃣ Cancel booking
            booking.setStatus(BookingStatus.CANCELLED);

            // 2️⃣ Restore doctor slot
            restoreDoctorSlot(booking);
        }

        bookingRepository.saveAll(staleBookings);

        System.out.println("⛔ Cancelled & restored slots for "
                + staleBookings.size() + " bookings");
    }

    // 🔓 Restore doctor slot when booking expires
    private void restoreDoctorSlot(Booking booking) {

        Doctor doctor = doctorClient.getDoctorById(booking.getDoctorId());

        boolean restored = false;

        for (DoctorAppointmentSchedule schedule : doctor.getAppointmentSchedules()) {
            if (schedule.getDate().isEqual(booking.getDate())) {
                for (TimeSlots slot : schedule.getTimeSlots()) {
                    if (slot.getTime().equals(booking.getTime())) {
                        slot.setAvailable(true); // ✅ RESTORE SLOT
                        restored = true;
                        break;
                    }
                }
            }
            if (restored) break;
        }

        // Persist updated schedule
        if (restored) {
            doctorClient.updateDoctorSchedule(
                    doctor.getId(),
                    doctor.getAppointmentSchedules()
            );
        }
    }
}