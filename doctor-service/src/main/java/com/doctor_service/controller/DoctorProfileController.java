package com.doctor_service.controller;

import com.doctor_service.dto.DoctorProfileRequestDto;
import com.doctor_service.dto.DoctorProfileResponseDto;
import com.doctor_service.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctor/profile")
public class DoctorProfileController {

    private final DoctorService doctorService;

    public DoctorProfileController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileResponseDto> createProfile(
            @RequestBody DoctorProfileRequestDto dto,
            Authentication authentication
    ) {
        String authEmail = authentication.getName();

        return ResponseEntity.ok(
                doctorService.createMyProfile(dto, authEmail)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileResponseDto> getProfile(
            Authentication authentication
    ) {
        String authEmail = authentication.getName();

        return ResponseEntity.ok(
                doctorService.getMyProfile(authEmail)
        );
    }

    @PutMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileResponseDto> updateProfile(
            @RequestBody DoctorProfileRequestDto dto,
            Authentication authentication
    ) {
        String authEmail = authentication.getName();

        return ResponseEntity.ok(
                doctorService.updateMyProfile(dto, authEmail)
        );
    }
}
