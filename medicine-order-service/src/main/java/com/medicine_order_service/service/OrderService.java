package com.medicine_order_service.service;

import com.medicine_order_service.client.PatientClient;
import com.medicine_order_service.client.PaymentClient;
import com.medicine_order_service.dto.Patient;
import com.medicine_order_service.dto.PaymentRequestDto;
import com.medicine_order_service.dto.PaymentResponseDto;
import com.medicine_order_service.entity.Cart;
import com.medicine_order_service.entity.CartItem;
import com.medicine_order_service.entity.Medicine;
import com.medicine_order_service.entity.Order;
import com.medicine_order_service.entity.OrderItem;
import com.medicine_order_service.repository.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MedicineRepository medicineRepository;
    private final PatientClient  patientClient;
    private final PaymentClient  paymentClient;

    private static final String PAYMENT_SERVICE = "paymentServiceCB";
    private static final String PATIENT_SERVICE = "patientServiceCB";

    @Transactional
    public PaymentResponseDto  placeOrder(String patientEmail){

        //Patient patient = patientClient.getPatientByEmail(patientEmail);

        Patient patient = getPatient(patientEmail); // use circuit breaker

        if(patient == null){
            throw new RuntimeException("Patient not found");
        }

        Long userId = patient.getId();

        Cart cart = cartRepository.findByUserId(userId);

        if(cart == null){
            throw new RuntimeException("Cart not found");
        }

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        if(items.isEmpty()){
            throw new RuntimeException("Cart is empty");
        }

        double total = 0;

        for(CartItem item : items){
            total += item.getQuantity() * item.getPrice();
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(total);
        //order.setStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING_PAYMENT");

        Order savedOrder = orderRepository.save(order);

        for(CartItem item : items){

            Medicine medicine = medicineRepository
                    .findById(item.getMedicineId())
                    .orElseThrow();

            if(medicine.getStock() < item.getQuantity()){
                throw new RuntimeException("Medicine out of stock");
            }

            //medicine.setStock(medicine.getStock() - item.getQuantity());
            // Reserve stock
            medicine.setReservedStock(medicine.getReservedStock() + item.getQuantity());
            medicineRepository.save(medicine);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setMedicineId(item.getMedicineId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());

            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteAll(items);

        PaymentRequestDto request = new PaymentRequestDto();

        request.setOrderId(savedOrder.getId());
        //request.setBookingId(savedOrder.getId()); // <--- ADD THIS
        request.setName("Medicine Order #" + savedOrder.getId());
        request.setAmount(BigDecimal.valueOf(savedOrder.getTotalAmount()));
        request.setQuantity(1L);
        request.setCurrency("INR");

        //PaymentResponseDto response = paymentClient.createPayment(request);
        PaymentResponseDto response = createPaymentWithCB(request); // circuit breaker

        return response;
    }

    @CircuitBreaker(name = PATIENT_SERVICE, fallbackMethod = "patientFallback")
    private Patient getPatient(String email) {
        return patientClient.getPatientByEmail(email);
    }

    public Patient patientFallback(String email, Throwable t){
        throw new RuntimeException("Patient service unavailable. Please try again later.");
    }

    @CircuitBreaker(name = PAYMENT_SERVICE, fallbackMethod = "paymentFallback")
    private PaymentResponseDto createPaymentWithCB(PaymentRequestDto request){
        return paymentClient.createPayment(request);
    }

    public PaymentResponseDto paymentFallback(PaymentRequestDto request, Throwable t){
        throw new RuntimeException("Payment service unavailable. Please try again later.");
    }

    public void markOrderPaid(Long orderId) {

        boolean updated = false;
        int retries = 3;

        while (!updated && retries-- > 0) {
            try {
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("Order not found"));

                if (order.getStatus().equals("CANCELLED")) {
                    throw new RuntimeException("Order expired");
                }


                List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
                for (OrderItem item : items) {
                    Medicine medicine = medicineRepository.findById(item.getMedicineId())
                            .orElseThrow();
                    medicine.setStock(medicine.getStock() - item.getQuantity());
                    medicine.setReservedStock(medicine.getReservedStock() - item.getQuantity());
                    medicineRepository.save(medicine);
                }
                order.setStatus("PAID");
                orderRepository.save(order);
                updated = true;
            } catch (OptimisticLockException ex) {

            }
        }
        if(!updated){
            throw new RuntimeException("Failed to mark order as paid due to concurrent updates");
        }

    }

    @Transactional
    public void cancelOrder(Long orderId){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(!order.getStatus().equals("PENDING_PAYMENT")){
            return;
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for(OrderItem item : items){
            Medicine medicine = medicineRepository.findById(item.getMedicineId())
                    .orElseThrow();
            // Release reserved stock
            medicine.setReservedStock(medicine.getReservedStock() - item.getQuantity());
            medicineRepository.save(medicine);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
}
