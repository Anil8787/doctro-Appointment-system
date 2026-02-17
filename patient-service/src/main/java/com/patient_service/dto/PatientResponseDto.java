package com.patient_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientResponseDto {
    private Long id;
    private String name;
    private String email;
    private String contact;
}
