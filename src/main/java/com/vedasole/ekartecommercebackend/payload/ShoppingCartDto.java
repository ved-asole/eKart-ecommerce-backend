package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

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
    private long customerId;
    private double total;
    private double discount;
    private List<ShoppingCartItemDto> shoppingCartItems;

}