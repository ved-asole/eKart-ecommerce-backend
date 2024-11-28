package com.vedasole.ekartecommercebackend.service.service_interface;

import com.vedasole.ekartecommercebackend.entity.OrderItem;
import com.vedasole.ekartecommercebackend.payload.OrderItemDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;

import java.util.List;

public interface OrderItemService {

    OrderItemDto createOrderItem(OrderItemDto orderItemDto);
    List<OrderItemDto> createAllOrderItemsFromCartItems(List<ShoppingCartItemDto> shoppingCartItemDtos);
    List<OrderItemDto> createAllOrderItems(List<OrderItemDto> orderItemDtos);
    OrderItemDto updateOrderItem(OrderItemDto orderItemDto);
    void deleteOrderItem(long orderItemId);
    void deleteAllOrderItems(long orderId);
    OrderItemDto getOrderItem(long orderId);
    List<OrderItemDto> getAllOrderItems(long orderId);
    OrderItem convertToOrderItem(OrderItemDto orderItemDto);
    OrderItemDto convertToOrderItemDto(OrderItem orderItem);

}