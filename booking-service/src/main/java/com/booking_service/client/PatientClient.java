package com.booking_service.client;

import com.booking_service.dto.Patient;
import com.booking_service.dto.PatientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PATIENT-SERVICE", configuration = com.booking_service.config.FeignConfig.class)
public interface PatientClient {
    @GetMapping("/api/v1/patient/getpatientbyid")
    Patient getPatientById(@RequestParam long id);
}

