package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.entity.Address;
import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.entity.Order;
import com.vedasole.ekartecommercebackend.entity.OrderItem;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.OrderDto;
import com.vedasole.ekartecommercebackend.repository.CustomerRepo;
import com.vedasole.ekartecommercebackend.repository.OrderItemRepo;
import com.vedasole.ekartecommercebackend.repository.OrderRepo;
import com.vedasole.ekartecommercebackend.service.service_interface.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CUSTOMER;
import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.ORDER;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final CustomerRepo customerRepo;
    private final OrderItemRepo orderItemsRepo;
    private final ModelMapper modelMapper;

    
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
                    log.error("Order not found for deleting with id: {}", orderId);
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
                    log.error("Order not found while fetching with id: {}", orderId);
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
     * This method retrieves the total number of orders in the system.
     *
     * @return the total number of orders.
     */
    @Override
    @Transactional(readOnly = true)
    public Long getTotalOrdersCount() {
        return this.orderRepo.count();
    }

    /**
     * This method retrieves the total income from all orders in the system.
     *
     * @return the total income.
     */
    @Override
    @Transactional(readOnly = true)
    public Long getTotalIncome() {
        Double totalIncome = this.orderRepo.getTotalIncome();
        return totalIncome.longValue();
    }

    /**
     * This method retrieves the total income from all orders in the system by month.
     *
     * @return the total income by month.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Double>> getTotalIncomeByMonth() {
        return this.orderRepo.getTotalIncomeByMonth();
    }

    /**
     * This method converts a OrderDto object to an Order object.
     *
     * @param orderDto the OrderDto object to convert
     * @return the converted Order object
     */
    @Override
    public Order convertToOrder(OrderDto orderDto) {
        return dtoToOrder(orderDto);
    }

    /**
     * This method maps an Order object to a OrderDto.
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
     * This method maps an OrderDto object to an Order.
     *
     * @param orderDto the OrderDto object to map
     * @return the mapped Order object
     */
    private Order dtoToOrder(OrderDto orderDto) {
        return this.modelMapper.map(orderDto, Order.class);
    }

}