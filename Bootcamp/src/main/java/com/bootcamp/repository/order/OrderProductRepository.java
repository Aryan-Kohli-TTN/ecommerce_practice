package com.bootcamp.repository.order;

import com.bootcamp.entity.order.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
    
}
