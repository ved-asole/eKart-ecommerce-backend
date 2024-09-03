package com.vedasole.ekartecommercebackend.payload;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Validated
@Data
public class AuthenticationRequest {

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
