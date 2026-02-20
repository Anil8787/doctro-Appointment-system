package com.doctor_service.controller;

import com.doctor_service.dto.CreateScheduleRequest;
import com.doctor_service.service.DoctorScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/doctor")
public class addSchedule {
    private final DoctorScheduleService  doctorScheduleService;

    @PostMapping("/{doctorId}/schedule")
    @PreAuthorize(
            "hasAuthority('ROLE_DOCTOR') and @doctorSecurity.isSelf(#doctorId, authentication)"
    )
    public ResponseEntity<String> addSchedule(
            @PathVariable Long doctorId,
            @RequestBody CreateScheduleRequest request
    ) {
        doctorScheduleService.addScheduleForLoggedInDoctor( request, doctorId );
        return ResponseEntity.ok("Schedule added successfully");
    }
}
