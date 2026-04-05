package com.patient_service.controller;

import com.patient_service.dto.PatientCreateRequest;
import com.patient_service.dto.PatientRequestDto;
import com.patient_service.dto.PatientResponseDto;
import com.patient_service.entity.Patient;
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

    //by auth-service this patient directly created ok
    @PostMapping("/create")
    public void createPatientInternal(
            @RequestBody PatientCreateRequest request
    ) {
        patientService.createPatientInternal(
                request.getAuthEmail(),
                request.getEmail()
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
    //http://localhost:5555/api/v1/patient/internal/getpatientbyid
    @GetMapping("/getpatientbyid")
    public ResponseEntity<PatientResponseDto> getPatientById(@RequestParam long id) {
        PatientResponseDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    @GetMapping("/getbyemail")
    public ResponseEntity<Patient> getPatientByEmail(@RequestParam String email) {
        Patient patient = patientService.getPatientByEmail(email); // service method
        return ResponseEntity.ok(patient);
    }


}
