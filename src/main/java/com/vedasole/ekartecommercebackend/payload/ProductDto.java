package com.vedasole.ekartecommercebackend.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vedasole.ekartecommercebackend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Product}
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Relation(itemRelation = "product", collectionRelation = "products")
public final class ProductDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 2079394134035966353L;

    private long productId;

    @NotNull(message = "Product name is required")
    @NotBlank(message = "Product name cannot be blank")
    @Size(
            min = 3,
            max = 100,
            message = "Product name must be between minimum of 3 characters and maximum of 100 characters"
    )
    private String name;

    @NotNull(message = "Product image is required")
    @NotBlank(message = "Product image cannot be blank")
    private String image;

    @NotNull(message = "Product description is required")
    @NotBlank(message = "Product description cannot be blank")
    @Size(max = 1000, message = "Product description cannot be longer than 1000 characters")
    private String desc;

    @NotNull(message = "Product price is required")
    @Min(value = 0, message = "Product price cannot be negative")
    private double price;

    @NotNull(message = "Product discount is required")
    @Min(value = 0, message = "Product discount cannot be negative")
    private double discount;

    @NotNull(message = "Product quantity is required")
    @Min(value = 0, message = "Product quantity cannot be negative")
    private int qtyInStock;

    @NotNull(message = "Category ID is required")
    @Min(value = 1, message = "Category ID cannot be negative")
    private long categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss:mm")
    private LocalDateTime updatedAt;

    public ProductDto(String name, String image, String desc, double price, double discount, int qtyInStock, long categoryId) {
        this.name = name;
        this.image = image;
        this.desc = desc;
        this.price = price;
        this.discount = discount;
        this.qtyInStock = qtyInStock;
        this.categoryId = categoryId;
    }
}