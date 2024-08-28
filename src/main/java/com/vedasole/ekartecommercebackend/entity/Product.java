package com.vedasole.ekartecommercebackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
@Entity
@Table(
        name = "product",
        indexes = {
                @Index(name = "product_name_idx", columnList = "name"),
                @Index(name = "product_desc_idx", columnList = "desc"),
                @Index(name = "product_name_desc_idx", columnList = "name, desc"),
                @Index(name = "product_category_idx", columnList = "category_id")
        }
)
public class Product {

    @Id
    @Column(name = "product_id" , updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", allocationSize = 0)
    private long productId;

    @NotNull(message = "Product name cannot be null")
    @NotBlank(message = "Product name cannot be blank")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Product image cannot be null")
    @NotBlank(message = "Product image cannot be blank")
    @Column(name = "image", nullable = false)
    private String image;

    @NotNull(message = "Product SKU cannot be null")
    @NotBlank(message = "Product SKU cannot be blank")
    @Column(name = "SKU", nullable = false, unique = true)
    private String sku;

    @Column(name = "desc", length = 1000)
    private String desc;

    @Column(name = "price", nullable = false)
    @NotNull(message = "Product price cannot be null")
    @Min(value = 0, message = "Product price must be greater than 0")
    private double price;

    @Column(name = "discount")
    @Min(value = 0, message = "Discount must not be negative")
    private double discount;

    @Column(name = "qtyInStock")
    @Min(value = 0, message = "Quantity in stock must be greater than 0")
    private int qtyInStock;

    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "create_dt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_dt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}