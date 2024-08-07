package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    void deleteAllByOrderOrderId(long orderId);
    List<OrderItem> findAllByOrderOrderId(long orderId);

}