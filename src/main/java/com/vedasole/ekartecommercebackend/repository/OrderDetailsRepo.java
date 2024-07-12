package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;


public interface OrderDetailsRepo extends JpaRepository<OrderDetail, Long> {
}