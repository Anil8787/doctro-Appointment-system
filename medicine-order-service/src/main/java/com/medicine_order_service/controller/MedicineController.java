package com.medicine_order_service.controller;

import com.medicine_order_service.entity.Medicine;
import com.medicine_order_service.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public List<Medicine> getAllMedicines(){
        return medicineService.getAllMedicines();
    }

    @GetMapping("/{id}")
    public Medicine getMedicine(@PathVariable Long id){
        return medicineService.getMedicine(id);
    }

    // 🔎 SEARCH MEDICINE
    @GetMapping("/search")
    public List<Medicine> searchMedicine(@RequestParam String name){
        return medicineService.searchMedicine(name);
    }
}
