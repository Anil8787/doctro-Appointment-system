package com.doctor_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class SearchResultDto {
    private long doctorId;
    private String name;
    private String specialization;
    private String qualification;
    private String area;
    private String city;
    private List<LocalDate> dates;
    private List<LocalTime> timeSlots;
    private String imageUrl;

}
