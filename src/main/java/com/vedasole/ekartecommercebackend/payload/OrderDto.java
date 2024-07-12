package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.Order;
import com.vedasole.ekartecommercebackend.entity.OrderDetail;
import com.vedasole.ekartecommercebackend.utility.AppConstant.OrderStatus;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link Order}
 */
public record OrderDto(long orderId, @NotNull CustomerDto customer, @NotNull Set<OrderDetail> orderDetails,
                       @NotNull AddressDto address, LocalDateTime orderDt, double total,
                       OrderStatus orderStatus) implements Serializable {
    @Serial
    private static final long serialVersionUID = 4761708818033261120L;
}