package com.medicine_order_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private double totalAmount;

    private String status; // PENDING_PAYMENT, PAID, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Version
    private Long version; // optimistic locking

}
