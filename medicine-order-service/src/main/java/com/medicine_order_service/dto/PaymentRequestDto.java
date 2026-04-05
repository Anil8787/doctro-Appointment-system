package com.medicine_order_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PaymentRequestDto {
    private Long orderId;   // IMPORTANT
    //private Long bookingId;
    private String name;
    private BigDecimal amount;
    private Long quantity;
    private String currency;

    private List<OrderItemDto> items;
}
