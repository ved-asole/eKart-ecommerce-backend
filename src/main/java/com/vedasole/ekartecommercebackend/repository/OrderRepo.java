package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomer_CustomerId(Long customerId);
    Page<Order> findAllByCustomer_CustomerId(Pageable pageable, Long customerId);
}