package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.ShoppingCartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

    @NotNull(message = "Product is required")
    private ProductDto product;

    @NotNull(message = "Cart id is required")
    @Positive(message = "Cart id must be greater than 0")
    private long cartId;

    @NotNull(message = "Product quantity is required")
    @Positive(message = "Product quantity must be greater than 0")
    private long quantity;

}