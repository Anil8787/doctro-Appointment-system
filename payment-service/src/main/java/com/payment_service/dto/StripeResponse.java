package com.payment_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeResponse {
    private String status;
    private String message;
    private String sessionId;
    private String sessionUrl;
}
