package com.payment_service.service;

import com.payment_service.dto.OrderPaymentRequest;
import com.payment_service.dto.ProductRequest;
import com.payment_service.dto.StripeResponse;
import com.payment_service.entity.Payment;
import com.payment_service.entity.PaymentStatus;
import com.payment_service.repository.PaymentRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    private final PaymentRepository paymentRepository;

    public StripeService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // For doctor appointment payments
    public StripeResponse checkoutAppointment(ProductRequest request) {
        return checkout(request.getAmount(), request.getCurrency(), request.getQuantity(),
                request.getName(), request.getBookingId(), null);
    }

    // For medicine order payments
    public StripeResponse checkoutOrder(OrderPaymentRequest request) {
        return checkout(request.getAmount(), request.getCurrency(), request.getQuantity(),
                "Medicine Order #" + request.getOrderId(), null, request.getOrderId());
    }

    private StripeResponse checkout(BigDecimal amount, String currency, Long quantity,
                                    String name, Long bookingId, Long orderId) {
        try {

            Long amountInSmallestUnit = amount.multiply(BigDecimal.valueOf(100)).longValue();

            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(name)
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(currency != null ? currency.toLowerCase() : "usd")
                            .setUnitAmount(amountInSmallestUnit)
                            .setProductData(productData)
                            .build();

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(quantity)
                            .setPriceData(priceData)
                            .build();

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("http://localhost:8084/product/v1/success?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl("http://localhost:8084/product/v1/cancel")
                            .addLineItem(lineItem)
                            .build();

            Session session = Session.create(params);

            Payment payment = Payment.builder()
                    .bookingId(bookingId)
                    .orderId(orderId)
                    .amount(amount)
                    .currency(currency)
                    .stripePaymentIntentId(session.getPaymentIntent())
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

        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }
}