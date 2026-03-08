package com.bootcamp.repository.order;

import com.bootcamp.entity.order.OrderStatusInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderStatusInfoRepository extends JpaRepository<OrderStatusInfo, UUID> {
    
}
