package com.auth_service.controller;

import com.auth_service.dto.DoctorCreateRequest;
import com.auth_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // http://localhost:5555/api/v1/admin/doctors
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctor(
            @RequestBody DoctorCreateRequest request
    ) {
        adminService.createDoctor(request);
        return ResponseEntity.ok("Doctor created");
    }
}
