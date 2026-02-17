package com.payment_service.controller;

import com.payment_service.client.BookingClient;
import com.payment_service.dto.ProductRequest;
import com.payment_service.dto.StripeResponse;
import com.payment_service.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/v1")
public class ProductCheckoutController {

    private final StripeService stripeService;
    private final BookingClient  bookingClient;

    public ProductCheckoutController(StripeService stripeService, BookingClient bookingClient) {
        this.stripeService = stripeService;
        this.bookingClient = bookingClient;
    }

    //http://localhost:8084/product/v1/checkout
    @PostMapping("/checkout")
    public StripeResponse checkoutProducts(@RequestBody ProductRequest productRequest) {
        return stripeService.checkoutProducts(productRequest);
    }

    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(
            @RequestParam("session_id") String sessionId) {

        try {
            Session session = Session.retrieve(sessionId);

            if ("paid".equalsIgnoreCase(session.getPaymentStatus())) {
                bookingClient.confirmBooking(session.getId());
                System.out.println("✅ Payment successful");
                // TODO: update booking / order status
                return ResponseEntity.ok("Payment successful");
            }

            return ResponseEntity.badRequest().body("Payment not completed");

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Stripe error occurred");
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel() {
        System.out.println("❌ Payment cancelled");
        return ResponseEntity.ok("Payment cancelled");
    }
}
