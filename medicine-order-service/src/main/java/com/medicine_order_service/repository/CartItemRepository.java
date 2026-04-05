package com.medicine_order_service.repository;

import com.medicine_order_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository
        extends JpaRepository<CartItem,Long> {

    List<CartItem> findByCartId(Long cartId);
}
