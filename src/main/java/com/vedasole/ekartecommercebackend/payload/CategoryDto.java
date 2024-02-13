package com.vedasole.ekartecommercebackend.payload;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link com.vedasole.ekartecommercebackend.entity.Category}
 */
public record CategoryDto(long categoryId, String name, String image, String desc,
                          long parentCategory, boolean isActive) implements Serializable {
    @Serial
    private static final long serialVersionUID = -6361844320830928689L;
}