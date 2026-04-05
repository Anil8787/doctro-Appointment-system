package com.medicine_order_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {
    private String status;
    private String sessionId;
    private String sessionUrl;
}
