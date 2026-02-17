package com.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DoctorAppointmentSchedule {

    private Long id;
    private Doctor doctor;
    private List<TimeSlots> timeSlots;
    private LocalDate date;

}
