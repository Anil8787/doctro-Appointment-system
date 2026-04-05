package com.medicine_order_service.dto;

import lombok.Data;

@Data
public class CartRequest {

    //private Long userId;

    private Long medicineId;

    private int quantity;
}
