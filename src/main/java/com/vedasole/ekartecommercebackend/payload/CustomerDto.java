package com.vedasole.ekartecommercebackend.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.utility.AppConstant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    @NotBlank(message = "First name is required")
    @Size(
            min = 3,
            max = 20,
            message = "First name must be between minimum of 3 characters and maximum of 20 characters"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(
            min = 3,
            max = 20,
            message = "Last name must be between minimum of 3 characters " +
                    "and maximum of 20 characters"
    )
    private String lastName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Email address is not valid",
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )
    private String email;

    private String password;

    @NotNull(message = "Phone number is required")
    @Pattern(
            regexp = "^(\\+?\\d{0,3}?[- ]?)\\d{10}$",
            message = "Phone number must be a valid 10-digit number"
    )
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonIncludeProperties({"cartId"})
    private ShoppingCartDto shoppingCart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}