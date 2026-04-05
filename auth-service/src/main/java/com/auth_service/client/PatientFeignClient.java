package com.auth_service.client;

import com.auth_service.config.FeignConfig;
import com.auth_service.dto.PatientCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "patient-service",configuration = FeignConfig.class)
public interface PatientFeignClient {
    @PostMapping("/api/v1/patient/create")
    void createPatient(@RequestBody PatientCreateRequest request);
}
