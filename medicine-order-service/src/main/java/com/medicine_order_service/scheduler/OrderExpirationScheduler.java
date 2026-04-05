package com.medicine_order_service.scheduler;

import com.medicine_order_service.entity.Order;
import com.medicine_order_service.repository.OrderRepository;
import com.medicine_order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpirationScheduler {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void cancelExpiredOrders(){

        List<Order> orders = orderRepository
                .findByStatusAndCreatedAtBefore("PENDING_PAYMENT", LocalDateTime.now().minusMinutes(2));

        for(Order order : orders){
            try {
                orderService.cancelOrder(order.getId());
                log.info("❌ Order auto cancelled: {}", order.getId());
            } catch (Exception e) {
                log.error("Failed to auto-cancel order {}: {}", order.getId(), e.getMessage());
            }
        }
    }
}