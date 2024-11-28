package com.vedasole.ekartecommercebackend.service.service_interface;

import com.vedasole.ekartecommercebackend.entity.ShoppingCartItem;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;

import java.util.List;

public interface ShoppingCartItemService {

    ShoppingCartItemDto createShoppingCartItem(ShoppingCartItemDto shoppingCartItemDto);
    List<ShoppingCartItemDto> createShoppingCartWithAllItems(List<ShoppingCartItemDto> shoppingCartItemDtos);
    ShoppingCartItemDto updateShoppingCartItem(ShoppingCartItemDto shoppingCartItemDto);
    void deleteShoppingCartItem(long cartItemId);
    void deleteAllShoppingCartItems(long cartId);
    ShoppingCartItemDto getShoppingCartItem(long cartItemId);
    List<ShoppingCartItemDto> getAllShoppingCartItems(long cartId);
    ShoppingCartItem convertToShoppingCartItem(ShoppingCartItemDto shoppingCartItemDto);
    ShoppingCartItemDto convertToShoppingCartItemDto(ShoppingCartItem shoppingCartItem);
}