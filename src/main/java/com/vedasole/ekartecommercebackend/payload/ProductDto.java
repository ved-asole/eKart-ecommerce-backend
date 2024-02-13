package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.Product;
import javax.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link Product}
 */
public record ProductDto(long productId, String name, String image, String desc, double price, int qtyInStock,
                         @NotNull CategoryDto category) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2079394134035966353L;
}