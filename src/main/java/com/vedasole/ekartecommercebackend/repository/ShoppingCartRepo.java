package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ShoppingCartRepo extends JpaRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByCustomer_CustomerId(long customerId);

    void deleteByCustomer_CustomerId(long customerId);

}