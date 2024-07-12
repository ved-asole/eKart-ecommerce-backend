package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.ShoppingCartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link ShoppingCartItem}
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Relation(itemRelation = "shoppingCartItem", collectionRelation = "shoppingCartItems")
public class ShoppingCartItemDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -2647249272100904224L;

    private long cartItemId;

    @NotNull(message = "Product id is required")
    @Min(value = 1, message = "Product id cannot be negative")
    private long productId;

    @NotNull(message = "Cart id is required")
    @Min(value = 1, message = "Cart id cannot be negative")
    private long cartId;

    @NotNull(message = "Product quantity is required")
    @Min(value = 0, message = "Product quantity cannot be negative")
    private long quantity;

}