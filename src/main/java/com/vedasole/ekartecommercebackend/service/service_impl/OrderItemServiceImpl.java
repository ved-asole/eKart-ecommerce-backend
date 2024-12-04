package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.entity.Order;
import com.vedasole.ekartecommercebackend.entity.OrderItem;
import com.vedasole.ekartecommercebackend.entity.Product;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.OrderItemDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;
import com.vedasole.ekartecommercebackend.repository.OrderItemRepo;
import com.vedasole.ekartecommercebackend.repository.OrderRepo;
import com.vedasole.ekartecommercebackend.service.service_interface.OrderItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.ORDER_DETAIL;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepo orderItemRepo;
    private final OrderRepo orderRepo;
    private final ModelMapper modelMapper;

    /**
     * Creates a new order detail in the database.
     *
     * @param orderItemDto The order detail data transfer object (DTO) containing the necessary information to create a new order detail.
     * @return The DTO of the newly created order detail.
     */
    @Override
    public OrderItemDto createOrderItem(OrderItemDto orderItemDto) {
        OrderItem savedOrderItem = this.orderItemRepo.save(dtoToOrderItem(orderItemDto));
        this.orderRepo.findById(savedOrderItem.getOrder().getOrderId())
                .ifPresentOrElse(
                        savedOrderItem::setOrder,
                        () -> {
                            throw new ResourceNotFoundException(
                                    ORDER_DETAIL.getValue(),
                                    "orderId",
                                    savedOrderItem.getOrder().getOrderId()
                            );
                        });
        this.orderItemRepo.save(savedOrderItem);
        log.info("Order detail {} created successfully", savedOrderItem.getOrderItemId());
        return orderItemToDto(savedOrderItem);
    }

    /**
     * Creates a list of new order details in the database from a list of shopping cart items.
     *
     * @param shoppingCartItemDtos The list of shopping cart item data transfer objects (DTOs) containing the necessary information to create new order details.
     * @return The list of DTOs of the newly created order details.
     */
    @Override
    public List<OrderItemDto> createAllOrderItemsFromCartItems(List<ShoppingCartItemDto> shoppingCartItemDtos) {
        List<OrderItemDto> orderItems = shoppingCartItemDtos.stream()
                .map(shoppingCartItemDto -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(this.modelMapper.map(shoppingCartItemDto.getProduct(), Product.class));
                    orderItem.setQuantity(shoppingCartItemDto.getQuantity());
                    return orderItemToDto(orderItem);
                })
                .toList();
        return createAllOrderItems(orderItems);
    }

    /**
     * Creates a list of new order details in the database.
     *
     * @param orderItemDtos The list of order detail data transfer objects (DTOs) containing the necessary information to create new order details.
     * @return The list of DTOs of the newly created order details.
     */
    @Override
    public List<OrderItemDto> createAllOrderItems(List<OrderItemDto> orderItemDtos) {
        List<OrderItem> orderItems = orderItemDtos.stream()
                .map(this::dtoToOrderItem)
                .toList();
        List<OrderItemDto> savedOrderItemsDto = this.orderItemRepo.saveAll(orderItems).stream()
                .map(this::orderItemToDto)
                .toList();
        if (savedOrderItemsDto.isEmpty()) log.error("Order details not created");
        else log.info("Order details created successfully");
        return savedOrderItemsDto;
    }

    /**
     * Updates an existing order detail in the database.
     *
     * @param orderItemDto The order detail data transfer object (DTO) containing the necessary information to update an existing order detail.
     * @return The DTO of the updated order detail.
     */
    @Override
    public OrderItemDto updateOrderItem(OrderItemDto orderItemDto) {
        Order order = this.orderRepo.findById(orderItemDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderItemDto.getOrderId()));
        return this.orderItemRepo.findById(orderItemDto.getOrderItemId())
                .map(orderItem -> {
                    orderItem.setQuantity(orderItemDto.getQuantity());
                    orderItem.setProduct(this.modelMapper.map(orderItemDto.getProduct(), Product.class));
                    orderItem.setQuantity(orderItemDto.getQuantity());
                    orderItem.setOrder(order);
                    log.info("Order detail {} updated successfully", orderItemDto.getOrderItemId());
                    return orderItemToDto(this.orderItemRepo.save(orderItem));
                })
                .orElseThrow(() -> new RuntimeException("Order detail not found"));
    }

    /**
     * Deletes an existing order detail from the database.
     *
     * @param orderItemId The ID of the order detail to delete.
     */
    @Override
    public void deleteOrderItem(long orderItemId) {
        this.orderItemRepo.findById(orderItemId)
                .ifPresentOrElse(orderItem -> {
                    this.orderItemRepo.deleteById(orderItemId);
                    log.info("Order detail {} deleted successfully", orderItemId);
                }, () -> {
                    throw new ResourceNotFoundException(ORDER_DETAIL.getValue(), "id", orderItemId);
                });
    }

    /**
     * Deletes all order details for a specific order from the database.
     *
     * @param orderId The ID of the order for which to delete all order details.
     */
    @Override
    public void deleteAllOrderItems(long orderId) {
        this.orderItemRepo.deleteAllByOrderOrderId(orderId);
        log.info("All order details for order {} deleted successfully", orderId);
    }

    /**
     * Retrieves an order detail from the database.
     *
     * @param orderId The ID of the order detail to retrieve.
     * @return The DTO of the retrieved order detail.
     */
    @Override
    public OrderItemDto getOrderItem(long orderId) {
        return this.orderItemRepo.findById(orderId)
                .map(this::orderItemToDto)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_DETAIL.getValue(), "id", orderId));
    }

    /**
     * Retrieves all order items for a specific order from the database.
     *
     * @param orderId The ID of the order for which to retrieve all order items.
     * @return The list of DTOs of the retrieved order items.
     */
    @Override
    public List<OrderItemDto> getAllOrderItems(long orderId) {
        List<OrderItemDto> orderItemDtoList = this.orderItemRepo.findAllByOrderOrderId(orderId).stream()
                .map(this::orderItemToDto)
                .toList();
        if (orderItemDtoList.isEmpty()) log.error("Order details not found with id {}", orderId);
        else log.info("Order details found with id {}", orderId);
        return orderItemDtoList;
    }

    /**
     * Converts an order detail data transfer object (DTO) to an order detail entity.
     *
     * @param orderItemDto The order detail DTO to convert.
     * @return The order detail entity.
     */
    @Override
    public OrderItem convertToOrderItem(OrderItemDto orderItemDto) {
        return dtoToOrderItem(orderItemDto);
    }

    /**
     * Converts an order detail entity to an order detail data transfer object (DTO).
     *
     * @param orderItem The order detail entity to convert.
     * @return The order detail DTO.
     */
    @Override
    public OrderItemDto convertToOrderItemDto(OrderItem orderItem) {
        return orderItemToDto(orderItem);
    }

    private OrderItemDto orderItemToDto(OrderItem orderItem) {
        return this.modelMapper.map(orderItem, OrderItemDto.class);
    }

    private OrderItem dtoToOrderItem(OrderItemDto orderItemDto) {
        return this.modelMapper.map(orderItemDto, OrderItem.class);
    }

}