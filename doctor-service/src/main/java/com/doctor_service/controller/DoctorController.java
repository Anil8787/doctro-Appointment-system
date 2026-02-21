package com.doctor_service.controller;

import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    //http://localhost:8081/api/v1/doctors
//    @PostMapping
//    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
//        Doctor savedDoctor = doctorService.createDoctor(doctor);
//        return new  ResponseEntity<>(savedDoctor, HttpStatus.CREATED);
//    }

    //http://localhost:8081/api/va/doctor/getdoctorbyid
    @GetMapping("/getdoctorbyid")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or (hasAuthority('ROLE_DOCTOR') and @doctorSecurity.isSelf(#id, authentication))")
    public Doctor getDoctorById(@RequestParam long id) {
        return doctorService.getDoctorById(id);
    }

    // no user role check here
    @GetMapping("/internal/getdoctorbyid")
    public Doctor getDoctorByIdInternal(@RequestParam long id) {
        return doctorService.getDoctorById(id);
    }


    @PutMapping("/internal/updatedoctorschedule")
    public void updateDoctorScheduleInternal(
            @RequestParam("id") Long id,
            @RequestBody List<DoctorAppointmentSchedule> appointmentSchedules
    ) {
        doctorService.updateDoctorSchedule(id, appointmentSchedules);
    }


}
