package com.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatientCreateRequest {
    private String authEmail; // JWT subject
    private String email;
}
