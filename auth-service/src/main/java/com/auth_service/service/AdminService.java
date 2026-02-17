package com.auth_service.service;

import com.auth_service.dto.DoctorCreateRequest;
import com.auth_service.entity.Role;
import com.auth_service.entity.User;
import com.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createDoctor(DoctorCreateRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Doctor already exists");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        User doctor = new User();
        doctor.setEmail(req.getEmail());
        doctor.setPassword(passwordEncoder.encode(tempPassword));
        doctor.setRole(Role.DOCTOR); // ✅ set doctor role
        doctor.setEnabled(true);

        userRepository.save(doctor);

        // send email in real production
        System.out.println("Temp password = " + tempPassword);

    }
}
