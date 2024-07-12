package com.vedasole.ekartecommercebackend.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String firstName;

    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotNull(message = "Phone number is required")
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @JsonIgnore
    private ShoppingCartDto shoppingCart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDt;

    private AddressDto address;
}