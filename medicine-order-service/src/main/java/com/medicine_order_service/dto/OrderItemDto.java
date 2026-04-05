package com.medicine_order_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDto {

    private String name;
    private Long quantity;
    private BigDecimal price;

}
