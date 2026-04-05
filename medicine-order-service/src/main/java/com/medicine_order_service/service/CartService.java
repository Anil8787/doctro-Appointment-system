package com.medicine_order_service.service;

import com.medicine_order_service.client.PatientClient;
import com.medicine_order_service.dto.CartRequest;
import com.medicine_order_service.dto.Patient;
import com.medicine_order_service.entity.Cart;
import com.medicine_order_service.entity.CartItem;
import com.medicine_order_service.entity.Medicine;
import com.medicine_order_service.repository.CartItemRepository;
import com.medicine_order_service.repository.CartRepository;
import com.medicine_order_service.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MedicineRepository medicineRepository;
    private final PatientClient patientClient;

    public void addToCart(CartRequest request, String patientEmail){

        // 1️⃣ Call Patient Service using Feign
        Patient patient = patientClient.getPatientByEmail(patientEmail);

        if(patient == null){
            throw new RuntimeException("Patient not found");
        }

        // 2️⃣ Find cart by patientId
        Cart cart = cartRepository.findByUserId(patient.getId());

        // 3️⃣ Create cart if not exists
        if(cart == null){
            cart = new Cart();
            cart.setUserId(patient.getId());
            cart = cartRepository.save(cart);
        }

        // 4️⃣ Get medicine
        Medicine medicine = medicineRepository
                .findById(request.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        // 5️⃣ Add item to cart
        CartItem item = new CartItem();

        item.setCartId(cart.getId());
        item.setMedicineId(medicine.getId());
        item.setQuantity(request.getQuantity());
        item.setPrice(medicine.getPrice());

        cartItemRepository.save(item);
    }
}