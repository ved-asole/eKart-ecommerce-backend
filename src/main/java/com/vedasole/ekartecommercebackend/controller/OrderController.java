package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.payload.OrderDto;
import com.vedasole.ekartecommercebackend.service.service_interface.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(value = {
        "http://localhost:5173",
        "https://ekart.vedasole.me",
        "https://ekart-shopping.netlify.app",
        "https://develop--ekart-shopping.netlify.app"
}, allowCredentials = "true")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private static final String ORDER_ITEMS = "orderItems";

    @PostMapping
    //@PostAuthorize("hasRole('USER')")
    public ResponseEntity<EntityModel<OrderDto>> createOrder(
            @RequestBody @Valid OrderDto orderDto
    ) {
        OrderDto order = orderService.createOrder(orderDto);
        EntityModel<OrderDto> orderDtoEntityModel = EntityModel.of(
                order,
                linkTo(methodOn(this.getClass()).getOrder(order.getOrderId())).withSelfRel(),
                linkTo(methodOn(OrderItemController.class).getAllOrderItems(order.getOrderId())).withRel(ORDER_ITEMS)
        );
        return ResponseEntity.ok(orderDtoEntityModel);
    }

    @PutMapping("/{orderId}")
    //@PostAuthorize("hasRole('USER')")
    public ResponseEntity<EntityModel<OrderDto>> updateOrder(
            @PathVariable long orderId,
            @RequestBody @Valid OrderDto orderDto
    ) {
        orderDto.setOrderId(orderId);
        OrderDto order = orderService.updateOrder(orderDto);
        EntityModel<OrderDto> orderDtoEntityModel = EntityModel.of(
                order,
                linkTo(methodOn(this.getClass()).getOrder(orderId)).withSelfRel(),
                linkTo(methodOn(OrderItemController.class).getAllOrderItems(order.getOrderId())).withRel(ORDER_ITEMS)
        );
        return ResponseEntity.ok(orderDtoEntityModel);
    }

    @DeleteMapping("/{orderId}")
//    //@PostAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable long orderId
    ) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<EntityModel<OrderDto>> getOrder(
            @PathVariable long orderId
    ) {
        OrderDto order = orderService.getOrder(orderId);
        EntityModel<OrderDto> orderDtoEntityModel = EntityModel.of(
                order,
                linkTo(methodOn(this.getClass()).getOrder(orderId)).withSelfRel(),
                linkTo(methodOn(OrderItemController.class).getAllOrderItems(order.getOrderId())).withRel(ORDER_ITEMS)
        );
        return ResponseEntity.ok(orderDtoEntityModel);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CollectionModel<OrderDto>> getAllOrdersForCustomer(
            @PathVariable long customerId
    ) {
        List<OrderDto> allOrdersByCustomer = orderService.getAllOrdersByCustomer(customerId);
        CollectionModel<OrderDto> orderDtoCollectionModel = CollectionModel.of(
                allOrdersByCustomer,
                linkTo(methodOn(OrderController.class).getAllOrdersForCustomer(customerId)).withSelfRel()
        );
        return ResponseEntity.ok(orderDtoCollectionModel);
    }



    @GetMapping("/customer/{customerId}/page")
    public ResponseEntity<Page<EntityModel<OrderDto>>> getAllOrdersByCustomerPerPage(
            @PathVariable long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Page<OrderDto> orderDtoPage = orderService.getAllOrdersbyCustomerPerPage(customerId, page, size, sortBy, sortOrder);
        return getEntityModelPage(orderDtoPage);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<OrderDto>> getAllOrders() {
        List<OrderDto> orderDtoList = orderService.getAllOrders();
        CollectionModel<OrderDto> orderDtoCollectionModel = CollectionModel.of(
                orderDtoList,
                linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel()
        );
        return ResponseEntity.ok(orderDtoCollectionModel);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalOrdersCount() {
        return ResponseEntity.ok(orderService.getTotalOrdersCount());
    }

    @GetMapping("/income")
    public ResponseEntity<Long> getTotalIncome() {
        return ResponseEntity.ok(orderService.getTotalIncome());
    }

    @GetMapping("/income-by-month")
    public ResponseEntity<List<Map<String, Double>>> getTotalIncomeByMonth() {
        return ResponseEntity.ok(orderService.getTotalIncomeByMonth());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<EntityModel<OrderDto>>> getAllOrdersPerPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Page<OrderDto> orderDtoPage = orderService.getAllOrdersPerPage(page, size, sortBy, sortOrder);
        return getEntityModelPage(orderDtoPage);
    }

    private ResponseEntity<Page<EntityModel<OrderDto>>> getEntityModelPage(Page<OrderDto> orderDtoPage) {
        Page<EntityModel<OrderDto>> entityModelPage = orderDtoPage.map(orderDto -> EntityModel.of(
                orderDto,
                linkTo(methodOn(OrderController.class).getOrder(orderDto.getOrderId())).withSelfRel(),
                linkTo(methodOn(OrderItemController.class).getAllOrderItems(orderDto.getOrderId())).withRel(ORDER_ITEMS)));
        return new ResponseEntity<>(entityModelPage, HttpStatus.OK);
    }

}