package com.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerifyDto {
    private String email;
    private String otpCode;
}
