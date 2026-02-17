package com.patient_service.repository;

import com.patient_service.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByAuthEmail(String authEmail);
    Optional<Patient> findByAuthEmail(String authEmail);
}