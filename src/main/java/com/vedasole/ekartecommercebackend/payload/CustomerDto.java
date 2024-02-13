package com.vedasole.ekartecommercebackend.payload;

import javax.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.vedasole.ekartecommercebackend.entity.Customer}
 */
public record CustomerDto(long customerId, String firstName, String lastName, long phoneNumber, String email,
                          LocalDateTime createDt, @NotNull AddressDto address) implements Serializable {
    @Serial
    private static final long serialVersionUID = -4970632778733952870L;
}