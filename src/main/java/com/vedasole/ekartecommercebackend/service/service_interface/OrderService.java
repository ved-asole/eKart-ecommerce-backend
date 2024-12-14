package com.vedasole.ekartecommercebackend.service.service_interface;

import com.vedasole.ekartecommercebackend.entity.Order;
import com.vedasole.ekartecommercebackend.payload.OrderDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto) throws IllegalArgumentException;
    OrderDto updateOrder(OrderDto orderDto);
    void deleteOrder(Long orderId);
    OrderDto getOrder(Long orderId);
    List<OrderDto> getAllOrdersByCustomer(Long customerId);
    List<OrderDto> getAllOrders();
    Page<OrderDto> getAllOrdersPerPage(int page, int size, String sortBy, String sortOrder);
    Page<OrderDto> getAllOrdersbyCustomerPerPage(long customerId, int page, int size, String sortBy, String sortOrder);
    Long getTotalOrdersCount();
    Long getTotalIncome();
    List<Map<String, Double>> getTotalIncomeByMonth();
    Order convertToOrder(OrderDto orderDto);
}