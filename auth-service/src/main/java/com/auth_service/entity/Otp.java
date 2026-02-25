package com.auth_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;           // Patient or Doctor email
    private String otpCode;         // OTP value
    private LocalDateTime expiryTime; // OTP expiry
    private boolean verified = false;
}
