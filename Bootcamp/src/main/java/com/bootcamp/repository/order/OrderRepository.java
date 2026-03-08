package com.bootcamp.repository.order;

import com.bootcamp.entity.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Orders, UUID> {

}
