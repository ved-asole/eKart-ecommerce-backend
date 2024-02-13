package com.vedasole.ekartecommercebackend.payload;

import com.vedasole.ekartecommercebackend.entity.Address;
import lombok.Builder;

import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link Address}
 */
@Builder
public record AddressDto(long addressId, String addLine1, String addLine2, @NotNull @NotEmpty @NotBlank String city,
                         @NotNull @NotEmpty @NotBlank String state, @NotNull @NotEmpty @NotBlank String country,
                         @Min(5) @Max(value = 6, message = "Postal code should be not greater than 5 characters") int postalCode) implements Serializable {
    @Serial
    private static final long serialVersionUID = 4607115799900867635L;
}