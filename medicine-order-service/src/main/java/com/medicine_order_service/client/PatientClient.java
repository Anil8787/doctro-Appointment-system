package com.medicine_order_service.client;

import com.medicine_order_service.config.FeignConfig;
import com.medicine_order_service.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PATIENT-SERVICE", configuration = FeignConfig.class)
public interface PatientClient {
    @GetMapping("/api/v1/patient/getpatientbyid")
    Patient getPatientById(@RequestParam long id);

    @GetMapping("/api/v1/patient/getbyemail")
    Patient getPatientByEmail(@RequestParam String email); // ✅ new
}

