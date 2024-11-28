package com.vedasole.ekartecommercebackend.service.service_interface;


import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;

public interface ShoppingCartService {

    ShoppingCart createCart(ShoppingCartDto shoppingCartDto);
    ShoppingCartDto createCartWithItems(ShoppingCartDto shoppingCartDto);
    ShoppingCartDto addOrUpdateItemInCart(ShoppingCartItemDto shoppingCartItemDto);
    ShoppingCartDto getCart(long customerId);
    void deleteCart(long customerId);
    ShoppingCart convertToShoppingCart(ShoppingCartDto shoppingCartDto);
}