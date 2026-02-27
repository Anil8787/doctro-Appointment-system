package com.auth_service.service;

import com.auth_service.config.JwtUtil;
import com.auth_service.dto.LoginResponse;
import com.auth_service.dto.OtpVerifyDto;
import com.auth_service.entity.Otp;
import com.auth_service.entity.User;
import com.auth_service.repository.OtpRepository;
import com.auth_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@AllArgsConstructor
@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;
//    public OtpService(OtpRepository otpRepository) {
//        this.otpRepository = otpRepository;
//    }

    // Generate 6-digit OTP
    private String generateOtp() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }
    public String createOtp(String email){
        String otpCode = generateOtp();
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setVerified(false);
        otpRepository.save(otp);
        // TODO: Send OTP via Email or SMS here
        System.out.println("Generated OTP for " + email + ": " + otpCode);

        emailService.sendOtpEmail(email, otpCode);

        return otpCode;
    }
    //verify otp
    // Step 2: Verify OTP and return LoginResponse with JWT
    public LoginResponse verifyOtp(OtpVerifyDto dto){
        // Step 1: Find latest unverified OTP for email
        Otp otp = otpRepository.findTopByEmailAndVerifiedFalseOrderByExpiryTimeDesc(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("No OTP requested or already verified"));

        // Step 2: Check expiry
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expired OTP");
        }

        // Step 3: Check OTP code
        if (!otp.getOtpCode().equals(dto.getOtpCode())) {
            throw new RuntimeException("Invalid OTP!");
        }

        // Step 4: Mark OTP as verified
        otp.setVerified(true);
        otpRepository.save(otp);

        // Step 5: Find user
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 6: Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), "ROLE_" + user.getRole().name());

        return new LoginResponse(token);
    }



}
