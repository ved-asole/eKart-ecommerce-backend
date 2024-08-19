package com.vedasole.ekartecommercebackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
    @Column(name = "name", nullable = false, length = 50)
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

    @Column(name = "create_dt", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_dt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
