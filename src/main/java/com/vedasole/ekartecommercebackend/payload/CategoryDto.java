package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.Category;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link Category}
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Relation(itemRelation = "category", collectionRelation = "categories")
public class CategoryDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6361844320830928689L;
    private long categoryId;
    private String name;
    private String image;
    private String desc;
    private Category parentCategory;
    private boolean active;

}