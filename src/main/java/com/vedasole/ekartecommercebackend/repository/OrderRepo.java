package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomer_CustomerId(Long customerId);
    Page<Order> findAllByCustomer_CustomerId(Pageable pageable, Long customerId);

    @Query(value = "SELECT SUM(o.total) FROM \"order\" o", nativeQuery = true)
    Double getTotalIncome();
}