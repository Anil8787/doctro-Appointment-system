package com.payment_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    private BigDecimal amount;
    private Long quantity;
    private String name;
    private String currency;

}
