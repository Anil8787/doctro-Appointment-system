package com.patient_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientCreateRequest {
    private String authEmail;
    private String email;
}
