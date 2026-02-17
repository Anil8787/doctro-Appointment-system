package com.doctor_service.security;

import com.doctor_service.entity.Doctor;
import com.doctor_service.repository.DoctorRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("doctorSecurity")
public class DoctorSecurity {

    private final DoctorRepository doctorRepository;

    public DoctorSecurity(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public boolean isSelf(Long doctorId, Authentication authentication) {
        String authEmail = authentication.getName();
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        return doctor != null && doctor.getAuthEmail().equals(authEmail);
    }
}
