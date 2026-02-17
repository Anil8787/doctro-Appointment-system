package com.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class Doctor {
    private Long id;

    private String name;

    private String specialization;

    private String qualification;

    private String contact;

    private String experience;

    private String url;

    private State state;

    private City city;



    private String address;

    private Area area;

    private List<DoctorAppointmentSchedule> appointmentSchedules;

}
