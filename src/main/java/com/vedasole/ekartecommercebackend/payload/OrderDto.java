package com.vedasole.ekartecommercebackend.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vedasole.ekartecommercebackend.entity.Order;
import com.vedasole.ekartecommercebackend.utility.AppConstant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link Order}
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Relation(itemRelation = "order", collectionRelation = "orders")
public class OrderDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 4761708818033261120L;

    private long orderId;

    @NotNull(message = "Customer is required")
    private CustomerDto customer;

    @NotNull(message = "Order details are required")
    @NotEmpty(message = "Order details should not be empty")
    private List<OrderItemDto> orderItems;

//    @NotNull(message = "Address is required") TODO: Until Address is implemented in Order
    private AddressDto address;

    @NotNull(message = "Total is required")
    @PositiveOrZero(message = "Total cannot be negative")
    private double total;

    private OrderStatus orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}