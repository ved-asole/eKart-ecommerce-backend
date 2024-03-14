package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.Product;
import lombok.Value;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link Product}
 */
@Value
@Relation(itemRelation = "product", collectionRelation = "products")
public final class ProductDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 2079394134035966353L;

    private final long productId;

    @NotNull(message = "Product name is required")
    @NotBlank(message = "Product name cannot be blank")
    private final String name;

    @NotNull(message = "Product image is required")
    @NotBlank(message = "Product image cannot be blank")
    private final String image;

    @NotNull(message = "Product description is required")
    @NotBlank(message = "Product description cannot be blank")
    private final String desc;

    @NotNull(message = "Product price is required")
    @Min(value = 0, message = "Product price cannot be negative")
    private final double price;

    @NotNull(message = "Product quantity is required")
    @Min(value = 0, message = "Product quantity cannot be negative")
    private final int qtyInStock;

    @NotNull(message = "Category ID is required")
    @Min(value = 1, message = "Category ID cannot be negative")
    private final long categoryId;
}