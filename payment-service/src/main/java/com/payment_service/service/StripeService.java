package com.payment_service.service;

import com.payment_service.dto.ProductRequest;
import com.payment_service.dto.StripeResponse;
import com.payment_service.entity.Payment;
import com.payment_service.entity.PaymentStatus;
import com.payment_service.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    private final PaymentRepository  paymentRepository;

    public StripeService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public StripeResponse checkoutProducts(ProductRequest productRequest) {

        // ✅ ADD THIS
        Long amountInSmallestUnit =
                productRequest.getAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .longValue();

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(
                                productRequest.getCurrency() != null
                                        ? productRequest.getCurrency().toLowerCase()
                                        : "usd"
                        )
                        .setUnitAmount(amountInSmallestUnit)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(productRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8084/product/v1/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("http://localhost:8084/product/v1/cancel")
                        .addLineItem(lineItem)
                        .build();

        try {
            // ✅ CREATE STRIPE SESSION
            Session session = Session.create(params);

            // 🔥 EXACT LINE YOU ASKED FOR
            String paymentIntentId = session.getPaymentIntent();
            // ✅ SAVE PAYMENT IN DATABASE
            Payment payment = Payment.builder()
                    .bookingId(productRequest.getBookingId())
                    .amount(productRequest.getAmount())
                    .currency(productRequest.getCurrency())
                    .stripePaymentIntentId(paymentIntentId) // ✅ STORED HERE
                    .stripeSessionId(session.getId())
                    .status(PaymentStatus.INITIATED)
                    .build();

            paymentRepository.save(payment);

            StripeResponse sr = new StripeResponse();
            sr.setStatus("SUCCESS");
            sr.setMessage("Payment session created");
            sr.setSessionId(session.getId());
            sr.setSessionUrl(session.getUrl());
            return sr;

        } catch (StripeException e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }
}

