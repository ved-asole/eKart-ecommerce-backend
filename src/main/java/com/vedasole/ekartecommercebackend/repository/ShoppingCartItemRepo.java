package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ShoppingCartItemRepo extends JpaRepository<ShoppingCartItem, Long> {
    List<ShoppingCartItem> findAllByShoppingCartCartId(long cartId);
    void deleteAllByShoppingCartCartId(long cartId);
}