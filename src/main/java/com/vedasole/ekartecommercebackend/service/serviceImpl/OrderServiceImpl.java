package com.vedasole.ekartecommercebackend.service.serviceImpl;

import com.vedasole.ekartecommercebackend.entity.*;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.OrderDto;
import com.vedasole.ekartecommercebackend.repository.CustomerRepo;
import com.vedasole.ekartecommercebackend.repository.OrderItemRepo;
import com.vedasole.ekartecommercebackend.repository.OrderRepo;
import com.vedasole.ekartecommercebackend.service.serviceInterface.OrderService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CUSTOMER;
import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.ORDER;

@Service
@AllArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final CustomerRepo customerRepo;
    private final OrderItemRepo orderItemsRepo;
    private final ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    
    /**
     * This method creates a new order in the system.
     *
     * @param orderDto the order data transfer object (DTO) containing the order details.
     * @return the created order DTO with the assigned order ID.
     * @throws IllegalArgumentException if the order DTO is null.
     */
    @Override
    public OrderDto createOrder(OrderDto orderDto) throws IllegalArgumentException {
        if (orderDto == null) throw new IllegalArgumentException("Order DTO cannot be null");

        Order savedOrder = this.orderRepo.save(dtoToOrder(orderDto));
        log.info("Order created successfully");
        return orderToDto(savedOrder);
    }

    /**
     * This method updates an existing order in the system.
     *
     * @param orderDto the order data transfer object (DTO) containing the updated order details.
     * @return the updated order DTO.
     */
    @Override
    public OrderDto updateOrder(OrderDto orderDto) {
        Order savedOrder = this.orderRepo.findById(orderDto.getOrderId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException(ORDER.getValue(), "id", orderDto.getOrderId())
                        );
        savedOrder.setOrderStatus(orderDto.getOrderStatus());
//        TODO: Remove null check after Address is implemented in Order
        if(orderDto.getAddress()!=null) savedOrder.setAddress(this.modelMapper.map(orderDto.getAddress(), Address.class));
        Customer customer = this.customerRepo.findById(orderDto.getCustomer().getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER.getValue(), "id", orderDto.getCustomer().getCustomerId()));
        savedOrder.setCustomer(customer);
        savedOrder.setOrderStatus(orderDto.getOrderStatus());
        Order finalSavedOrder = savedOrder;
        List<OrderItem> orderItems = orderDto.getOrderItems().stream()
                .map(orderItemDto -> {
                    OrderItem orderItem = this.modelMapper.map(orderItemDto, OrderItem.class);
                    orderItem.setOrder(finalSavedOrder);
                    return orderItem;
                })
                .toList();
        List<OrderItem> savedOrderItems = orderItemsRepo.saveAllAndFlush(orderItems);
        savedOrder.setOrderItems(savedOrderItems);
        savedOrder = this.orderRepo.save(savedOrder);

        log.info("Order {} updated successfully", orderDto.getOrderId());
        return orderToDto(savedOrder);
    }

    /**
     * This method deletes an existing order in the system.
     *
     * @param orderId the ID of the order to delete.
     */
    @Override
    public void deleteOrder(Long orderId) {
        this.orderRepo.findById(orderId)
                .ifPresentOrElse(order -> {
                    this.orderRepo.delete(order);
                    log.info("Order deleted successfully");
                }, () -> {
                    log.error("Order not found");
                    throw new ResourceNotFoundException("Order", "id", orderId);
                });
    }

    /**
     * This method retrieves an order by its ID.
     *
     * @param orderId the ID of the order to retrieve.
     * @return the order DTO.
     */
    @Override
    public OrderDto getOrder(Long orderId) {
        return this.orderRepo.findById(orderId)
                .map(this::orderToDto)
                .orElseThrow(() -> {
                    log.error("Order not found");
                    return new ResourceNotFoundException("Order", "id", orderId);
                });
    }

    /**
     * This method retrieves all orders by a customer.
     *
     * @param customerId the ID of the customer.
     * @return the list of order DTOs.
     */
    @Override
    public List<OrderDto> getAllOrdersByCustomer(Long customerId) {
        return this.orderRepo.findAllByCustomer_CustomerId(customerId)
                .stream()
                .map(this::orderToDto)
                .toList();
    }

    /**
     * This method retrieves all orders by a customer.
     *
     * @param customerId the ID of the customer.
     * @param page the page number.
     * @param size the page size.
     * @param sortBy the field to sort by.
     * @param sortOrder the sort order.
     * @return the list of order DTOs.
     */
    @Override
    public Page<OrderDto> getAllOrdersbyCustomerPerPage(long customerId, int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        Page<Order> ordersPage = orderRepo.findAllByCustomer_CustomerId(pageRequest, customerId);
        return ordersPage.map(this::orderToDto);
    }

    /**
     * This method retrieves all orders in the system.
     *
     * @return the list of order DTOs.
     */
    @Override
    public List<OrderDto> getAllOrders() {
        return this.orderRepo.findAll()
                .stream()
                .map(this::orderToDto)
                .toList();
    }

    /**
     * This method retrieves all orders in the system.
     *
     * @param page the page number.
     * @param size the page size.
     * @param sortBy the field to sort by.
     * @param sortOrder the sort order.
     * @return the list of order DTOs.
     */
    @Override
    public Page<OrderDto> getAllOrdersPerPage(int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        Page<Order> ordersPage = orderRepo.findAll(pageRequest);
        return ordersPage.map(this::orderToDto);
    }

    /**
     * This method converts a OrderDto object to a Order object.
     *
     * @param orderDto the OrderDto object to convert
     * @return the converted Order object
     */
    @Override
    public Order convertToOrder(OrderDto orderDto) {
        return dtoToOrder(orderDto);
    }

    /**
     * This method maps a Order object to a OrderDto.
     *
     * @param order the Order object to map
     * @return the mapped OrderDto object
     */
    private OrderDto orderToDto(Order order) {
        OrderDto orderDto = this.modelMapper.map(order, OrderDto.class);
        orderDto.setOrderId(order.getOrderId());
        return orderDto;
    }

    /**
     * This method maps a OrderDto object to a Order.
     *
     * @param orderDto the OrderDto object to map
     * @return the mapped Order object
     */
    private Order dtoToOrder(OrderDto orderDto) {
        return this.modelMapper.map(orderDto, Order.class);
    }

}
