package com.medicine_order_service.service;

import com.medicine_order_service.entity.Medicine;
import com.medicine_order_service.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public List<Medicine> getAllMedicines(){
        return medicineRepository.findAll();
    }

    public Medicine getMedicine(Long id){
        return medicineRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Medicine not found"));
    }
    public List<Medicine> searchMedicine(String name){
        return medicineRepository.findByNameContainingIgnoreCase(name);
    }
}
