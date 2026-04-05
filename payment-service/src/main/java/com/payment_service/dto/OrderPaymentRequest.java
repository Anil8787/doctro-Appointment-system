package com.payment_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderPaymentRequest {
    private Long orderId;
    private BigDecimal amount;
    private String currency;
    private Long quantity;

    private List<OrderItemDto> items;

}
