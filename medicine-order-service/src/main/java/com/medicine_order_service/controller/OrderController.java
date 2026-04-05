package com.medicine_order_service.controller;

import com.medicine_order_service.dto.PaymentResponseDto;
import com.medicine_order_service.entity.Order;
import com.medicine_order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/place")
    public PaymentResponseDto placeOrder(Authentication authentication){

        String patientEmail = authentication.getName();

        return orderService.placeOrder(patientEmail);
    }

    @PostMapping("/payment-success")
    public String paymentSuccess(@RequestParam Long orderId){

        orderService.markOrderPaid(orderId);

        return "Payment Successful. Order Confirmed.";
    }
}
