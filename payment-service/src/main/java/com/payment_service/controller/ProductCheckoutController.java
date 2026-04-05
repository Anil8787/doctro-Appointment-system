package com.payment_service.controller;

import com.payment_service.client.BookingClient;
import com.payment_service.client.MedicineClient;
import com.payment_service.dto.OrderPaymentRequest;
import com.payment_service.dto.ProductRequest;
import com.payment_service.dto.StripeResponse;
import com.payment_service.entity.Payment;
import com.payment_service.entity.PaymentStatus;
import com.payment_service.repository.PaymentRepository;
import com.payment_service.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/v1")
public class ProductCheckoutController {

    private final StripeService stripeService;
    private final BookingClient bookingClient;
    private final PaymentRepository paymentRepository;
    private final MedicineClient medicineClient;

    public ProductCheckoutController(StripeService stripeService,
                                     BookingClient bookingClient,
                                     PaymentRepository paymentRepository,
                                     MedicineClient medicineClient) {
        this.stripeService = stripeService;
        this.bookingClient = bookingClient;
        this.paymentRepository = paymentRepository;
        this.medicineClient = medicineClient;
    }

    // Doctor appointment
    @PostMapping("/checkout/appointment")
    public StripeResponse checkoutAppointment(@RequestBody ProductRequest request) {
        return stripeService.checkoutAppointment(request);
    }

    // Medicine order
    @PostMapping("/checkout/order")
    public StripeResponse checkoutOrder(@RequestBody OrderPaymentRequest request) {
        return stripeService.checkoutOrder(request);
    }

    @Transactional
    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam("session_id") String sessionId) {
        try {

            Session session = Session.retrieve(sessionId);

            if ("paid".equalsIgnoreCase(session.getPaymentStatus())
                    || "complete".equalsIgnoreCase(session.getStatus())) {

                Payment payment = paymentRepository.findByStripeSessionId(sessionId)
                        .orElseThrow(() -> new RuntimeException("Payment not found"));

                if (payment.getStatus() == PaymentStatus.SUCCESS) {
                    return ResponseEntity.ok("Already processed");
                }

                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);

                if (payment.getBookingId() != null && payment.getOrderId() == null) {

                    bookingClient.confirmBooking(payment.getBookingId());

                } else if (payment.getOrderId() != null && payment.getBookingId() == null) {

                    System.out.println("Calling medicine-order-service for orderId: " + payment.getOrderId());

                    medicineClient.paymentSuccess(payment.getOrderId());

                } else {

                    System.out.println("Warning: bookingId and orderId both null or both present");
                }

                return ResponseEntity.ok("Payment successful");
            }

            return ResponseEntity.badRequest().body("Payment not completed");

        } catch (Exception e) {
            e.printStackTrace();   // VERY IMPORTANT
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Stripe error occurred" + e.getMessage());
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel() {
        return ResponseEntity.ok("Payment cancelled");
    }
}