package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;


public interface ShoppingCartRepo extends JpaRepository<ShoppingCart, Long> {
}