package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.payload.OrderItemDto;
import com.vedasole.ekartecommercebackend.service.service_interface.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/order/{orderId}/items")
@CrossOrigin(value = {
        "http://localhost:5173",
        "https://ekart.vedasole.me",
        "https://ekart-shopping.netlify.app",
        "https://develop--ekart-shopping.netlify.app"
}, allowCredentials = "true")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<EntityModel<OrderItemDto>> createOrderItem(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderItemDto orderItemDto
    ){
        orderItemDto.setOrderId(orderId);
        OrderItemDto orderItem = orderItemService.createOrderItem(orderItemDto);
        EntityModel<OrderItemDto> orderItemDtoEntityModel = EntityModel.of(
                orderItem
//                ,
//                linkTo(methodOn(ShoppingCartItemController.class).getShoppingCartItem(shoppingCartItem.getCartId(), shoppingCartItem.getCartItemId())).withSelfRel(),
//                linkTo(methodOn(ShoppingCartController.class).getShoppingCart(shoppingCartItem.getCartId())).withRel("cart")
        );
        return new ResponseEntity<>(orderItemDtoEntityModel, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<EntityModel<OrderItemDto>> updateOrderItem(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderItemDto orderItemDto
    ){
        orderItemDto.setOrderId(orderId);
        OrderItemDto orderItem = orderItemService.updateOrderItem(orderItemDto);
        EntityModel<OrderItemDto> orderItemDtoEntityModel = EntityModel.of(
                orderItem
//                ,
//                linkTo(methodOn(ShoppingCartItemController.class).getShoppingCartItem(shoppingCartItem.getCartId(), shoppingCartItem.getCartItemId())).withSelfRel(),
//                linkTo(methodOn(ShoppingCartController.class).getShoppingCart(shoppingCartItem.getCartId())).withRel("cart")
        );
        return new ResponseEntity<>(orderItemDtoEntityModel, HttpStatus.OK);
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long orderItemId
    ) {
        orderItemService.deleteOrderItem(orderItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllOrderItems(
            @PathVariable Long orderId
    ) {
        orderItemService.deleteAllOrderItems(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<EntityModel<OrderItemDto>> getOrderItem(
            @PathVariable long orderId,
            @PathVariable long orderItemId
    ){
        OrderItemDto orderItem = orderItemService.getOrderItem(orderItemId);
        EntityModel<OrderItemDto> orderItemDtoEntityModel = EntityModel.of(
                orderItem,
                linkTo(methodOn(OrderItemController.class).getOrderItem(orderId, orderItemId)).withSelfRel()
//                ,
//                linkTo(methodOn(ShoppingCartController.class).getShoppingCart(orderId)).withRel("order")
        );
        return ResponseEntity.ok(orderItemDtoEntityModel);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<OrderItemDto>> getAllOrderItems(
            @PathVariable long orderId
    ){
        List<OrderItemDto> allOrderItems = orderItemService.getAllOrderItems(orderId);
        CollectionModel<OrderItemDto> orderItemDtoCollectionModel = CollectionModel.of(
                allOrderItems,
                linkTo(methodOn(OrderItemController.class).getAllOrderItems(orderId)).withSelfRel()
//                ,
//                linkTo(methodOn(ShoppingCartController.class).getShoppingCart(orderId)).withRel("order")
        );
        return ResponseEntity.ok(orderItemDtoCollectionModel);
    }

}