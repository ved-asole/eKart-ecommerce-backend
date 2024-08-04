package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link OrderDetail}
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Relation(itemRelation = "orderDetail", collectionRelation = "orderDetails")
public class OrderDetailDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5054394287337400890L;

    private long orderDetailId;

    private long orderId;

    @NotNull(message = "Product is required")
    private ProductDto product;

    @NotNull(message = "Product quantity is required")
    @Positive(message = "Product quantity must be greater than 0")
    private long quantity;

}