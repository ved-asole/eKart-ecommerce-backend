package com.vedasole.ekartecommercebackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * Entity class for Category
 */
@Builder
@Validated
@Entity
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@Table(name = "category")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = -5392075886775352349L;

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", allocationSize = 0)
    private long categoryId;

    @Column(nullable = false, length = 20)
    private String name;

    private String image;

    @Column(length = 1000)
    private String desc;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @JsonProperty
    private boolean active;

    public Category() {}

    public Category(String name, String image, String desc, Category parentCategory, boolean active) {
        this.name = name;
        this.image = image;
        this.desc = desc;
        this.parentCategory = parentCategory;
        this.active = active;
    }
}
