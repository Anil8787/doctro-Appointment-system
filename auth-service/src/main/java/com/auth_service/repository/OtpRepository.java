package com.auth_service.repository;

import com.auth_service.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

//    Optional<Otp> findByEmailAndOtpCodeAndVerifiedFalse(String email, String otpCode);
        Optional<Otp> findTopByEmailAndVerifiedFalseOrderByExpiryTimeDesc(String email);
}