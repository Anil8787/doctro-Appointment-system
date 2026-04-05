package com.medicine_order_service.controller;

import com.medicine_order_service.dto.CartRequest;
import com.medicine_order_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/add")
    public String addToCart(@RequestBody CartRequest request, Authentication authentication){

        String patientEmail = authentication.getName();
        cartService.addToCart(request,patientEmail);

        return "Medicine added to cart";
    }
}
