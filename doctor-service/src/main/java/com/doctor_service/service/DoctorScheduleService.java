package com.doctor_service.service;

import com.doctor_service.dto.CreateScheduleRequest;
import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.doctor_service.repository.DoctorAppointmentScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorScheduleService {

    private final DoctorAppointmentScheduleRepository scheduleRepository;
    private final DoctorService doctorService;

    public DoctorScheduleService(
            DoctorAppointmentScheduleRepository scheduleRepository,
            DoctorService doctorService) {
        this.scheduleRepository = scheduleRepository;
        this.doctorService = doctorService;
    }

    public void addScheduleForLoggedInDoctor(
            CreateScheduleRequest request,
            Long loggedInDoctorId) {

        Doctor doctor = doctorService.getDoctorById(loggedInDoctorId);

        DoctorAppointmentSchedule schedule = new DoctorAppointmentSchedule();
        schedule.setDoctor(doctor);
        schedule.setDate(request.getDate());

        List<TimeSlots> slots = request.getTimeSlots()
                .stream()
                .map(time -> {
                    TimeSlots slot = new TimeSlots();
                    slot.setTime(time);
                    slot.setDoctorAppointmentSchedule(schedule);
                    slot.setAvailable(true);   // ✅ THIS LINE IS MANDATORY
                    return slot;
                })
                .toList();

        schedule.setTimeSlots(slots);

        scheduleRepository.save(schedule); // cascade saves slots
    }
}

