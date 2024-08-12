package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link ShoppingCart}
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Relation(itemRelation = "shoppingCart")
public class ShoppingCartDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 2151030173521724266L;

    private long cartId;
    @NotNull(message = "Customer id is required")
    @Positive(message = "Customer id must be greater than or equal to 1")
    private long customerId;
    @NotNull(message = "Total is required")
    @PositiveOrZero(message = "Total cannot be negative")
    private double total;
    @NotNull(message = "Discount is required")
    @PositiveOrZero(message = "Discount cannot be negative")
    private double discount;
    private List<ShoppingCartItemDto> shoppingCartItems;

}