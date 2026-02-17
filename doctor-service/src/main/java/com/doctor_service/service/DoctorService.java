package com.doctor_service.service;

import com.doctor_service.dto.DoctorProfileRequestDto;
import com.doctor_service.dto.DoctorProfileResponseDto;
import com.doctor_service.dto.SearchResultDto;
import com.doctor_service.entity.*;
import com.doctor_service.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    private final TimeSlotsRepository timeSlotsRepository;

    public DoctorService(
            DoctorRepository doctorRepository,
            TimeSlotsRepository timeSlotsRepository,
            StateRepository stateRepository,
            CityRepository cityRepository,
            AreaRepository areaRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.timeSlotsRepository = timeSlotsRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
    }


    /* =====================================================
   ADMIN / INTERNAL APIs (ENTITY BASED)
   ===================================================== */

//    @Transactional
//    public Doctor createDoctor(Doctor doctor) {
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String email = auth.getName(); // ← JWT subject (email)
//
//        doctor.setAuthEmail(email);   // 🔥 REQUIRED
//
//        // ---------- Handle State ----------
//        if (doctor.getState() != null && doctor.getState().getName() != null) {
//            State state = stateRepository
//                    .findByName(doctor.getState().getName())
//                    .orElseGet(() -> stateRepository.save(doctor.getState()));
//            doctor.setState(state);
//        }
//
//        // ---------- Handle City ----------
//        if (doctor.getCity() != null && doctor.getCity().getName() != null) {
//            City city = cityRepository
//                    .findByName(doctor.getCity().getName())
//                    .orElseGet(() -> cityRepository.save(doctor.getCity()));
//            doctor.setCity(city);
//        }
//
//        // ---------- Handle Area ----------
//        if (doctor.getArea() != null && doctor.getArea().getName() != null) {
//            Area area = areaRepository
//                    .findByName(doctor.getArea().getName())
//                    .orElseGet(() -> areaRepository.save(doctor.getArea()));
//            doctor.setArea(area);
//        }
//
//        // ---------- Fix Doctor → Schedule → Slot mapping ----------
//        if (doctor.getAppointmentSchedules() != null) {
//            doctor.getAppointmentSchedules().forEach(schedule -> {
//
//                schedule.setDoctor(doctor);
//
//                if (schedule.getTimeSlots() != null) {
//                    schedule.getTimeSlots().forEach(slot -> {
//                        slot.setDoctorAppointmentSchedule(schedule);
//                    });
//                }
//            });
//        }
//
//        return doctorRepository.save(doctor);
//    }


    public Doctor getDoctorById(long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }


    /* =====================================================
       PUBLIC DOCTOR SEARCH (NO AUTH REQUIRED)
       ===================================================== */

    public List<SearchResultDto> searchDoctor(String specialization, String areaName) {

        LocalDate today = LocalDate.now();
        List<SearchResultDto> result = new ArrayList<>();

        List<Doctor> doctors =
                doctorRepository.findBySpecializationAndArea(specialization, areaName);

        for (Doctor doctor : doctors) {

            SearchResultDto dto = new SearchResultDto();
            List<LocalDate> validDates = new ArrayList<>();
            List<LocalTime> allTimeSlots = new ArrayList<>();

            for (DoctorAppointmentSchedule schedule : doctor.getAppointmentSchedules()) {

                LocalDate scheduleDate = schedule.getDate();
                if (scheduleDate.isBefore(today)) continue;

                validDates.add(scheduleDate);

                LocalTime now = LocalTime.now();
                List<TimeSlots> timeSlots =
                        timeSlotsRepository.getAllTimeSlots(schedule.getId());

                for (TimeSlots slot : timeSlots) {

                    if (scheduleDate.isEqual(today)) {
                        if (slot.getTime().isAfter(now)) {
                            allTimeSlots.add(slot.getTime());
                        }
                    } else {
                        allTimeSlots.add(slot.getTime());
                    }
                }
            }

            dto.setDoctorId(doctor.getId());
            dto.setName(doctor.getName());
            dto.setSpecialization(doctor.getSpecialization());
            dto.setQualification(doctor.getQualification());
            dto.setArea(doctor.getArea().getName());
            dto.setCity(doctor.getCity().getName());
            dto.setDates(validDates);
            dto.setTimeSlots(allTimeSlots);

            result.add(dto);
        }

        return result;
    }

    /* =====================================================
       DOCTOR PROFILE (AUTH REQUIRED)
       ===================================================== */

    public DoctorProfileResponseDto createMyProfile(
            DoctorProfileRequestDto dto,
            String authEmail
    ) {

        if (doctorRepository.existsByAuthEmail(authEmail)) {
            throw new RuntimeException("Doctor profile already exists");
        }


        Doctor doctor = new Doctor();
        doctor.setAuthEmail(authEmail);
        mapRequestToEntity(dto, doctor);

        Doctor saved = doctorRepository.save(doctor);
        return mapToResponse(saved);
    }

    public DoctorProfileResponseDto getMyProfile(String authEmail) {

        Doctor doctor = doctorRepository.findByAuthEmail(authEmail)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        return mapToResponse(doctor);
    }

    public DoctorProfileResponseDto updateMyProfile(
            DoctorProfileRequestDto dto,
            String authEmail
    ) {

        Doctor doctor = doctorRepository.findByAuthEmail(authEmail)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        mapRequestToEntity(dto, doctor);

        Doctor updated = doctorRepository.save(doctor);
        return mapToResponse(updated);
    }

    /* =====================================================
       MAPPERS (INDUSTRY STANDARD)
       ===================================================== */

    private void mapRequestToEntity(DoctorProfileRequestDto dto, Doctor doctor) {

        doctor.setName(dto.getName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setQualification(dto.getQualification());
        doctor.setContact(dto.getContact());
        doctor.setExperience(dto.getExperience());
        doctor.setUrl(dto.getUrl());
        doctor.setAddress(dto.getAddress());

        // ---------- Handle State ----------
        if (dto.getState() != null && !dto.getState().isBlank()) {
            State state = stateRepository.findByName(dto.getState())
                    .orElseGet(() -> {
                        State newState = new State();
                        newState.setName(dto.getState());
                        return stateRepository.save(newState);
                    });
            doctor.setState(state);
        }

        // ---------- Handle City ----------
        if (dto.getCity() != null && !dto.getCity().isBlank()) {
            City city = cityRepository.findByName(dto.getCity())
                    .orElseGet(() -> {
                        City newCity = new City();
                        newCity.setName(dto.getCity());
                        // optional: link to state
                        //newCity.setState(doctor.getState()); // if you later add state field in City
                        return cityRepository.save(newCity);
                    });
            doctor.setCity(city);
        }

        // ---------- Handle Area ----------
        if (dto.getArea() != null && !dto.getArea().isBlank()) {
            Area area = areaRepository.findByName(dto.getArea())
                    .orElseGet(() -> {
                        Area newArea = new Area();
                        newArea.setName(dto.getArea());
                        // optional: link to city
                        //newArea.setCity(doctor.getCity()); // if you later add city field in Area
                        return areaRepository.save(newArea);
                    });
            doctor.setArea(area);
        }
    }


    private DoctorProfileResponseDto mapToResponse(Doctor doctor) {

        DoctorProfileResponseDto dto = new DoctorProfileResponseDto();

        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setQualification(doctor.getQualification());
        dto.setContact(doctor.getContact());
        dto.setExperience(doctor.getExperience());
        dto.setUrl(doctor.getUrl());
        dto.setAddress(doctor.getAddress());

        dto.setState(doctor.getState().getName());
        dto.setCity(doctor.getCity().getName());
        dto.setArea(doctor.getArea().getName());

        return dto;
    }
}
