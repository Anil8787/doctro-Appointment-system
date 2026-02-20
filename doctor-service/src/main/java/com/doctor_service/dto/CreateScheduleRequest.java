package com.doctor_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class CreateScheduleRequest {
    private LocalDate date;
    private List<LocalTime> timeSlots;
}
