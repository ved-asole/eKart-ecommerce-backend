package com.vedasole.ekartecommercebackend.service.serviceInterface;

import com.vedasole.ekartecommercebackend.entity.Order;
import com.vedasole.ekartecommercebackend.payload.OrderDto;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto) throws IllegalArgumentException;
    OrderDto updateOrder(OrderDto orderDto);
    void deleteOrder(Long orderId);
    OrderDto getOrder(Long orderId);
    List<OrderDto> getAllOrdersByCustomer(Long customerId);
    List<OrderDto> getAllOrders();
    Order convertToOrder(OrderDto orderDto);

}
