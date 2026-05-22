package com.vedasole.ekartecommercebackend.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequestDto {
    @NotBlank(message = "Token is required")
    private String token;
    @NotBlank(message = "New Password is required")
    private String newPassword;
}