package com.doctor_service.repository;

import com.doctor_service.entity.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    //search by specialization + city name (case-insensitive)
    @Query("SELECT d FROM Doctor d " +
            "WHERE LOWER(d.specialization) = LOWER(:specialization)" +
            "AND LOWER(d.area.name) = LOWER(:areaName)")
    List<Doctor> findBySpecializationAndArea(@Param("specialization") String specialization, @Param("areaName") String areaName);

    Optional<Doctor> findByAuthEmail(String authEmail);

    boolean existsByAuthEmail(String authEmail);

    // existing search query stays untouched
}