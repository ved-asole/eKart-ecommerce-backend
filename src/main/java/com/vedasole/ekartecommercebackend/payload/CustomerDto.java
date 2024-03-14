package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.Customer;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Customer}
 */
@Value
@Builder
@Relation(itemRelation = "customer", collectionRelation = "customers")
public class CustomerDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -4970632778733952870L;


    private final long customerId;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name cannot be blank")
    private final String firstName;

    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name cannot be blank")
    private final String lastName;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    private final String email;

    private final String password;

    @NotNull(message = "Phone number is required")
    @NotBlank(message = "Phone number cannot be blank")
    private final long phoneNumber;

    private final LocalDateTime createDt;

    private final AddressDto address;
}