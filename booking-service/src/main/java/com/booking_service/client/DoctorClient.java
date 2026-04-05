package com.booking_service.client;

import com.booking_service.config.FeignConfig;
import com.booking_service.dto.Doctor;
import com.booking_service.dto.DoctorAppointmentSchedule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "DOCTOR-SERVICE",configuration = FeignConfig.class)
public interface DoctorClient {
    @GetMapping("/api/v1/doctor/internal/getdoctorbyid")
    public Doctor getDoctorById(@RequestParam long id);

    @GetMapping("/api/v1/doctor/internal/getdoctorbyEmail")
    public Doctor getDoctorByEmail(@RequestParam String doctorEmail);

    @PutMapping("/api/v1/doctor/internal/updatedoctorschedule")
    void updateDoctorSchedule(
            @RequestParam("id") Long id,
            @RequestBody List<DoctorAppointmentSchedule> appointmentSchedules
    );

}
