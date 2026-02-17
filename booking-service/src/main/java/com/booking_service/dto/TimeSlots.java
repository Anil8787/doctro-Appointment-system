package com.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class TimeSlots {

    private Long id;
    private LocalTime time;
    private DoctorAppointmentSchedule doctorAppointmentSchedule;

}
