package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;
import com.vedasole.ekartecommercebackend.service.service_interface.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping("/api/v1/shopping-cart")
@CrossOrigin(value = {
        "http://localhost:5173",
        "https://ekart.vedasole.me",
        "https://ekart-shopping.netlify.app",
        "https://develop--ekart-shopping.netlify.app"
}, allowCredentials = "true")
@RequiredArgsConstructor
public class ShoppingCartController {

    private static final String CART_ITEMS = "cart-items";
    private final ShoppingCartService shoppingCartService;

    @PostMapping({"/customer/{customerId}"})
    //@PostAuthorize("hasRole('USER')")
    public ResponseEntity<EntityModel<ShoppingCartDto>> createShoppingCart(
            @PathVariable long customerId,
            @RequestBody @Valid ShoppingCartDto shoppingCartDto
    ) {
        shoppingCartDto.setCustomerId(customerId);
        ShoppingCartDto shoppingCart = shoppingCartService.createCartWithItems(shoppingCartDto);
        EntityModel<ShoppingCartDto> shoppingCartDtoEntityModel = EntityModel.of(
                shoppingCart,
                linkTo(methodOn(this.getClass()).getShoppingCart(shoppingCart.getCustomerId())).withSelfRel(),
                linkTo(methodOn(ShoppingCartItemController.class).getAllShoppingCartItems(shoppingCart.getCartId())).withRel(CART_ITEMS)
        );
        return ResponseEntity.ok(shoppingCartDtoEntityModel);
    }

    @PutMapping("/{cartId}")
    //@PostAuthorize("hasRole('USER')")
    public ResponseEntity<EntityModel<ShoppingCartDto>> updateCart(
            @PathVariable long cartId,
            @RequestBody @Valid ShoppingCartItemDto shoppingCartItemDto
    ) {
        shoppingCartItemDto.setCartId(cartId);
        ShoppingCartDto shoppingCart = shoppingCartService.addOrUpdateItemInCart(shoppingCartItemDto);
        EntityModel<ShoppingCartDto> shoppingCartDtoEntityModel = EntityModel.of(
                shoppingCart,
                linkTo(methodOn(this.getClass()).getShoppingCart(cartId)).withSelfRel(),
                linkTo(methodOn(ShoppingCartItemController.class).getAllShoppingCartItems(shoppingCart.getCartId())).withRel(CART_ITEMS)
        );
        return ResponseEntity.ok(shoppingCartDtoEntityModel);
    }

    @DeleteMapping("/customer/{customerId}")
//    //@PostAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShoppingCart(
            @PathVariable long customerId
    ) {
        shoppingCartService.deleteCart(customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<EntityModel<ShoppingCartDto>> getShoppingCart(
            @PathVariable long customerId
    ) {
        ShoppingCartDto shoppingCart = shoppingCartService.getCart(customerId);
        EntityModel<ShoppingCartDto> shoppingCartDtoEntityModel = EntityModel.of(
                shoppingCart,
                linkTo(methodOn(this.getClass()).getShoppingCart(customerId)).withSelfRel(),
                linkTo(methodOn(ShoppingCartItemController.class).getAllShoppingCartItems(shoppingCart.getCartId())).withRel(CART_ITEMS)
        );
        return ResponseEntity.ok(shoppingCartDtoEntityModel);
    }
}