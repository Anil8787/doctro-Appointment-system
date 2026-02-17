package com.patient_service.controller;

import com.patient_service.dto.PatientRequestDto;
import com.patient_service.dto.PatientResponseDto;
import com.patient_service.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // POST http://localhost:8082/api/v1/patient/create-patient
    @PostMapping("/create-patient")
    public ResponseEntity<PatientResponseDto> createPatient(
            @RequestBody PatientRequestDto dto,
            Authentication authentication) {

        String authEmail = authentication.getName(); // from JWT subject

        return ResponseEntity.ok(
                patientService.createPatient(dto, authEmail)
        );
    }

    // GET http://localhost:8082/api/v1/patient/me
    @GetMapping("/me")
    public ResponseEntity<PatientResponseDto> getMyProfile(
            Authentication authentication) {

        String authEmail = authentication.getName();

        return ResponseEntity.ok(
                patientService.getMyProfile(authEmail)
        );
    }

    // PUT http://localhost:8082/api/v1/patient/me
    @PutMapping("/me")
    public ResponseEntity<PatientResponseDto> updateMyProfile(
            @RequestBody PatientRequestDto dto,
            Authentication authentication) {

        String authEmail = authentication.getName();

        return ResponseEntity.ok(
                patientService.updateMyProfile(dto, authEmail)
        );
    }
}
