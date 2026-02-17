package com.doctor_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorProfileResponseDto {

    private Long id;
    private String name;
    private String specialization;
    private String qualification;
    private String contact;
    private String experience;
    private String url;
    private String address;

    private String state;
    private String city;
    private String area;
}
