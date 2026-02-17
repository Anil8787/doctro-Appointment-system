package com.doctor_service.controller;

import com.doctor_service.dto.SearchResultDto;
import com.doctor_service.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
public class SearchController {
    private final DoctorService doctorService;
    public SearchController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // Example:
    // http://localhost:8081/api/v1/doctor/search?specialization=cardiologist&areaName=btm
    @GetMapping("/search")
    public ResponseEntity<List<SearchResultDto>> searchDoctors(
            @RequestParam String specialization,
            @RequestParam String areaName
    ){
        List<SearchResultDto> result=doctorService.searchDoctor(specialization,areaName);
        return ResponseEntity.ok(result);
    }
}
