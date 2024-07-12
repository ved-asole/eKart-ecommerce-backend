package com.vedasole.ekartecommercebackend.service.serviceInterface;

import com.vedasole.ekartecommercebackend.entity.ShoppingCartItem;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;

import java.util.List;

public interface ShoppingCartItemService {

    ShoppingCartItemDto createShoppingCartItem(ShoppingCartItemDto shoppingCartItemDto);
    List<ShoppingCartItemDto> createAllShoppingCartItem(List<ShoppingCartItemDto> shoppingCartItemDtos);
    ShoppingCartItemDto updateShoppingCartItem(ShoppingCartItemDto shoppingCartItemDto);
    void deleteShoppingCartItem(long cartItemId);
    ShoppingCartItemDto getShoppingCartItem(long cartItemId);
    ShoppingCartItem convertToShoppingCartItem(ShoppingCartItemDto shoppingCartItemDto);
    ShoppingCartItemDto convertToShoppingCartItemDto(ShoppingCartItem shoppingCartItem);
}
