package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.Address;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link Address}
 */
@Value //To make the class immutable
@AllArgsConstructor
@Relation(itemRelation = "address", collectionRelation = "addresses")
public class AddressDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 4607115799900867635L;

    long addressId;

    @NotNull(message = "Address Line 1 is required")
    @NotBlank(message = "Address Line 1 cannot be blank")
    String addLine1;

    @NotNull(message = "Address Line 2 is required")
    @NotBlank(message = "Address Line 2 cannot be blank")
    String addLine2;

    @NotNull(message = "City is required")
    @NotBlank(message = "City cannot be blank")
    String city;

    @NotNull(message = "State is required")
    @NotBlank(message = "State cannot be blank")
    String state;

    @NotNull(message = "Country is required")
    @NotBlank(message = "Country cannot be blank")
    String country;

    @NotNull(message = "Postal code is required")
    @NotBlank(message = "Postal code cannot be blank")
    @Min(5)
    @Max(value = 6, message = "Postal code should be not greater than 5 characters")
    int postalCode;
}