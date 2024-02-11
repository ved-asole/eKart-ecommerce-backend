package com.vedasole.ekartecommercebackend.entity;

import javax.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id" , updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", allocationSize = 0)
    private long productId;

    private String name;
    private String image;
    @Column(name = "SKU", nullable = false)
    private String sku;
    private String desc;
    private double price;
    private int qtyInStock;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}