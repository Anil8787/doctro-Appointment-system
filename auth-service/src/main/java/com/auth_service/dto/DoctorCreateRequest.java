package com.auth_service.dto;

import lombok.Data;

@Data
public class DoctorCreateRequest {
    private String email;
    private String name;
    private String specialization;
}
