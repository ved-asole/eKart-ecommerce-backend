package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.Category;
import lombok.Builder;
import lombok.Value;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link Category}
 */
@Value
@Builder
@Relation(itemRelation = "category", collectionRelation = "categories")
public class CategoryDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6361844320830928689L;

    private long categoryId;
    
    @NotNull(message = "Category name is required")
    @NotBlank(message = "Category name cannot be blank")
    private String name;
    
    @NotNull(message = "Category image is required")
    @NotBlank(message = "Category image cannot be blank")
    private String image;
    
    @NotNull(message = "Category is required")
    @NotBlank(message = "Category cannot be blank")
    private String desc;

    private Category parentCategory;
    
    @NotNull(message = "Category should be either active or non-active")
    private boolean active;

}