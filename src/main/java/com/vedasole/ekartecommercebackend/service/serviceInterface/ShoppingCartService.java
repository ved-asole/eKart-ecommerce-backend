package com.vedasole.ekartecommercebackend.service.serviceInterface;


import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;

import java.util.List;

public interface ShoppingCartService {

    ShoppingCart createCart(ShoppingCartDto shoppingCartDto);
    ShoppingCartDto createCartWithItems(ShoppingCartDto shoppingCartDto);
    ShoppingCartDto addOrUpdateItemInCart(ShoppingCartItemDto shoppingCartItemDto);
    ShoppingCartDto removeItemFromCart(long cartId, long itemId);
    ShoppingCartDto getCart(long cartId);
    boolean deleteCart(long cartId);
    List<ShoppingCartItemDto> getAllShoppingCartItems(long cartId);
    ShoppingCart convertToShoppingCart(ShoppingCartDto shoppingCartDto);
}
