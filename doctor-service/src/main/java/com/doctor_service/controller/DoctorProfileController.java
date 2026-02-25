package com.doctor_service.controller;

import com.doctor_service.dto.DoctorProfileRequestDto;
import com.doctor_service.dto.DoctorProfileResponseDto;
import com.doctor_service.service.DoctorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/doctor/profile")
public class DoctorProfileController {

    private final DoctorService doctorService;

    public DoctorProfileController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileResponseDto> createProfile(
            @RequestBody DoctorProfileRequestDto dto,   // ✅ FIX

            Authentication authentication
    ) throws IOException {
        String authEmail = authentication.getName();

        return ResponseEntity.ok(
                doctorService.createMyProfile(dto,authEmail)
        );
    }

    @PostMapping(
            value = "/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileResponseDto> uploadProfilePhoto(
            @RequestParam("photo") MultipartFile photo,
            Authentication authentication
    ) throws IOException {

        String authEmail = authentication.getName();

        return ResponseEntity.ok(
                doctorService.uploadProfilePhoto(photo, authEmail)
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
