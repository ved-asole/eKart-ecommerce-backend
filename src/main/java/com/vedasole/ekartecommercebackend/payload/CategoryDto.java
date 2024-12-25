package com.vedasole.ekartecommercebackend.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vedasole.ekartecommercebackend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Category}
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Relation(itemRelation = "category", collectionRelation = "categories")
public class CategoryDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6361844320830928689L;

    private long categoryId;
    
    @NotNull(message = "Category name is required")
    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
    private String name;

    private String image;
    
    @NotNull(message = "Category is required")
    @NotBlank(message = "Category cannot be blank")
    private String desc;

    private Category parentCategory;
    
    @NotNull(message = "Category should be either active or non-active")
    private boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss:mm")
    private LocalDateTime updatedAt;

    public CategoryDto(String name, String image, String desc, Category parentCategory, boolean active) {
        this.name = name;
        this.image = image;
        this.desc = desc;
        this.parentCategory = parentCategory;
        this.active = active;
    }

}