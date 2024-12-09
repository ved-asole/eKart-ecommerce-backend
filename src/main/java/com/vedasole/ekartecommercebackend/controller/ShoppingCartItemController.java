package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;
import com.vedasole.ekartecommercebackend.service.service_interface.ShoppingCartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping("/api/v1/shopping-cart/{cartId}/items")
@CrossOrigin(value = {
        "http://localhost:5173",
        "https://ekart.vedasole.me",
        "https://ekart-shopping.netlify.app",
        "https://develop--ekart-shopping.netlify.app"
}, allowCredentials = "true")
@RequiredArgsConstructor
public class ShoppingCartItemController {

    private final ShoppingCartItemService shoppingCartItemService;

    @PostMapping
    public ResponseEntity<EntityModel<ShoppingCartItemDto>> createShoppingCartItem(
            @PathVariable Long cartId,
            @RequestBody @Valid ShoppingCartItemDto shoppingCartItemDto
    ){
        shoppingCartItemDto.setCartId(cartId);
        ShoppingCartItemDto shoppingCartItem = shoppingCartItemService.createShoppingCartItem(shoppingCartItemDto);
        EntityModel<ShoppingCartItemDto> shoppingCartItemDtoEntityModel = EntityModel.of(
                shoppingCartItem,
                linkTo(methodOn(ShoppingCartItemController.class).getShoppingCartItem(shoppingCartItem.getCartId(), shoppingCartItem.getCartItemId())).withSelfRel(),
                linkTo(methodOn(ShoppingCartController.class).getShoppingCart(shoppingCartItem.getCartId())).withRel("cart")
        );
        return new ResponseEntity<>(shoppingCartItemDtoEntityModel, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<EntityModel<ShoppingCartItemDto>> updateShoppingCartItem(
            @PathVariable Long cartId,
            @RequestBody @Valid ShoppingCartItemDto shoppingCartItemDto
    ){
        shoppingCartItemDto.setCartId(cartId);
        ShoppingCartItemDto shoppingCartItem = shoppingCartItemService.updateShoppingCartItem(shoppingCartItemDto);
        EntityModel<ShoppingCartItemDto> shoppingCartItemDtoEntityModel = EntityModel.of(
                shoppingCartItem,
                linkTo(methodOn(ShoppingCartItemController.class).getShoppingCartItem(shoppingCartItem.getCartId(), shoppingCartItem.getCartItemId())).withSelfRel(),
                linkTo(methodOn(ShoppingCartController.class).getShoppingCart(shoppingCartItem.getCartId())).withRel("cart")
        );
        return new ResponseEntity<>(shoppingCartItemDtoEntityModel, HttpStatus.OK);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteShoppingCartItem(
            @PathVariable Long cartId,
            @PathVariable Long cartItemId
    ) {
        shoppingCartItemService.deleteShoppingCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllShoppingCartItems(
            @PathVariable Long cartId
    ) {
        shoppingCartItemService.deleteAllShoppingCartItems(cartId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{cartItemId}")
    public ResponseEntity<EntityModel<ShoppingCartItemDto>> getShoppingCartItem(
            @PathVariable long cartId,
            @PathVariable long cartItemId
    ){
        ShoppingCartItemDto item = shoppingCartItemService.getShoppingCartItem(cartItemId);
        EntityModel<ShoppingCartItemDto> shoppingCartItemDtoEntityModel = EntityModel.of(
                item,
                linkTo(methodOn(ShoppingCartItemController.class).getShoppingCartItem(item.getCartId(), item.getCartItemId())).withSelfRel(),
                linkTo(methodOn(ShoppingCartController.class).getShoppingCart(item.getCartId())).withRel("cart")
        );
        return ResponseEntity.ok(shoppingCartItemDtoEntityModel);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ShoppingCartItemDto>> getAllShoppingCartItems(
            @PathVariable Long cartId
    ){
        List<ShoppingCartItemDto> allShoppingCartItems = shoppingCartItemService.getAllShoppingCartItems(cartId);
        CollectionModel<ShoppingCartItemDto> shoppingCartItemDtoCollectionModel = CollectionModel.of(
                allShoppingCartItems,
                linkTo(methodOn(ShoppingCartItemController.class).getAllShoppingCartItems(cartId)).withSelfRel()
        );
        return ResponseEntity.ok(shoppingCartItemDtoCollectionModel);
    }
}