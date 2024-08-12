package com.vedasole.ekartecommercebackend.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.utility.AppConstant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Customer}
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Relation(itemRelation = "customer", collectionRelation = "customers")
public class CustomerDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -4970632778733952870L;

    private long customerId;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name cannot be blank")
    @Size(
            min = 3,
            max = 20,
            message = "First name must be between minimum of 3 characters and maximum of 20 characters"
    )
    private String firstName;

    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name cannot be blank")
    @Size(
            min = 3,
            max = 20,
            message = "Last name must be between minimum of 3 characters " +
                    "and maximum of 20 characters"
    )
    private String lastName;

    @NotNull(message = "Email is required")
    @NotEmpty(message = "Email address must not be empty")
    @Email(message = "Email address is not valid",
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )
    private String email;

    @NotEmpty(message = "Password cannot be blank")
    @Size(
            min = 3,
            max = 20,
            message = "Password must be between minimum of 3 characters " +
                    "and maximum of 20 characters"
    )
    private String password;

    @NotNull(message = "Phone number is required")
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    private Role role;

    @JsonIgnore
    private ShoppingCartDto shoppingCart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private AddressDto address;
}