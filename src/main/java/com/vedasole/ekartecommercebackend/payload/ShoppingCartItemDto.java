package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.ShoppingCartItem;
import com.vedasole.ekartecommercebackend.payload.ProductDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import javax.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link ShoppingCartItem}
 */
public record ShoppingCartItemDto(long shoppingCartItemId, @NotNull ProductDto product,
                                  @NotNull ShoppingCartDto shoppingCart, int quantity) implements Serializable {
    @Serial
    private static final long serialVersionUID = -2647249272100904224L;
}