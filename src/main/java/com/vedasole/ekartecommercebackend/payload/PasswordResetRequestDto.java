package com.vedasole.ekartecommercebackend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequestDto {
    @NotBlank(message = "Token is required")
    private String token;
    @NotBlank(message = "New Password is required")
    private String newPassword;
}