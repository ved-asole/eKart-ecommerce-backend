package com.vedasole.ekartecommercebackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@Table(name = "category")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = -5392075886775352349L;

    @Id
    @Column(name = "category_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", allocationSize = 0)
    private long categoryId;

    @NotNull
    @NotBlank
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @NotNull
    @NotBlank
    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "desc", length = 1000)
    private String desc;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @NotNull
    @Column(name = "active", nullable = false)
    private boolean active;

}
