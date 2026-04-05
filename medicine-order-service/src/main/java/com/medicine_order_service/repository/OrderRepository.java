package com.medicine_order_service.repository;

import com.medicine_order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository
        extends JpaRepository<Order,Long> {
    //List<Order> findByStatus(String ststus);
    List<Order> findByStatusAndCreatedAtBefore(String pendingPayment, LocalDateTime localDateTime);
}
