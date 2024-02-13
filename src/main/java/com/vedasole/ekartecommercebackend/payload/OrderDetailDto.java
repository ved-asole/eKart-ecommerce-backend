package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.OrderDetail;
import javax.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link OrderDetail}
 */
public record OrderDetailDto(long orderDetailsId, @NotNull OrderDto order, long productProductId, String productName,
                             String productImage, String productDesc, double productPrice, int productQtyInStock,
                             CategoryDto productCategory) implements Serializable {
    @Serial
    private static final long serialVersionUID = -5054394287337400890L;
}