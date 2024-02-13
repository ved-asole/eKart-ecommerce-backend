package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.entity.ShoppingCartItem;
import javax.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link ShoppingCart}
 */
public record ShoppingCartDto(long cartId, @NotNull CustomerDto customerId,
                              @NotNull Set<ShoppingCartItem> shoppingCartItems) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2151030173521724266L;


}