package com.patient_service.service;

import com.patient_service.dto.PatientRequestDto;
import com.patient_service.dto.PatientResponseDto;
import com.patient_service.entity.Patient;
import com.patient_service.repository.PatientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto dto, String authEmail) {

        if (patientRepository.existsByAuthEmail(authEmail)) {
            throw new RuntimeException("Patient profile already exists");
        }

        Patient patient = new Patient();
        BeanUtils.copyProperties(dto, patient);
        patient.setAuthEmail(authEmail);

        Patient saved = patientRepository.save(patient);

        PatientResponseDto response = new PatientResponseDto();
        BeanUtils.copyProperties(saved, response);
        return response;
    }

    @Transactional(readOnly = true)
    public PatientResponseDto getMyProfile(String authEmail) {

        Patient patient = patientRepository.findByAuthEmail(authEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        PatientResponseDto dto = new PatientResponseDto();
        BeanUtils.copyProperties(patient, dto);
        return dto;
    }

    @Transactional
    public PatientResponseDto updateMyProfile(PatientRequestDto dto, String authEmail) {

        Patient patient = patientRepository.findByAuthEmail(authEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setName(dto.getName());
        patient.setContact(dto.getContact());
        patient.setEmail(dto.getEmail());

        Patient updated = patientRepository.save(patient);

        PatientResponseDto response = new PatientResponseDto();
        BeanUtils.copyProperties(updated, response);
        return response;
    }
}


