package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.payload.*;
import com.vedasole.ekartecommercebackend.service.service_interface.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller for handling authentication related requests.
 *
 * @author Ved Asole
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(
        value = {
                "http://localhost:5173",
                "https://ekart.vedasole.me",
                "https://ekart-shopping.netlify.app",
                "https://develop--ekart-shopping.netlify.app"
        },
        allowCredentials = "true",
        exposedHeaders = {"Authorization"}
)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticates a user and returns an authentication token.
     *
     * @param authRequest The authentication request containing the user's credentials.
     * @param request The HTTP request.
     * @return A ResponseEntity containing the authentication response and an HTTP status code.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest authRequest,
            HttpServletRequest request
    ) {
        AuthenticationResponse authenticationResponse = this.authenticationService.authenticate(authRequest, request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authenticationResponse.getToken());
        return new ResponseEntity<>(authenticationResponse, headers, HttpStatus.OK);
    }

    /**
     * Checks if a user is authenticated based on the provided request.
     *
     * @param request The HTTP request.
     * @return A ResponseEntity containing a boolean indicating whether the user is authenticated or not, and an HTTP status code.
     */
    @GetMapping("/check-token")
    public ResponseEntity<Boolean> isAuthenticated(HttpServletRequest request) {
        boolean isValid = this.authenticationService.authenticate(request);
        if(isValid) return new ResponseEntity<>(true, HttpStatus.OK);
        else return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/generate-reset-token")
    public ResponseEntity<ApiResponse> generatePasswordResetToken(@RequestBody ResetTokenRequestDto resetTokenRequestDto) {
        try {
            authenticationService.generatePasswordResetToken(resetTokenRequestDto.email());
            return new ResponseEntity<>(new ApiResponse("Password reset token sent successfully", true), HttpStatus.OK);
        } catch (MessagingException e) {
            return new ResponseEntity<>(new ApiResponse("Cannot send token to the emailId: " + resetTokenRequestDto.email(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        boolean tokenValid = authenticationService.isResetTokenValid(passwordResetRequestDto.getToken());
        if (!tokenValid) {
            return new ResponseEntity<>(new ApiResponse("Invalid or expired token", false), HttpStatus.BAD_REQUEST);
        }
        boolean isReset = authenticationService.resetPassword(passwordResetRequestDto.getToken(), passwordResetRequestDto.getNewPassword());
        if (isReset) {
            return new ResponseEntity<>(new ApiResponse("Password reset successfully", true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Invalid or expired token", false), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validate-reset-token")
    public ResponseEntity<Boolean> isResetTokenValid(@RequestBody ValidateTokenRequestDto validateTokenRequestDto) {
        boolean isTokenValid = authenticationService.isResetTokenValid(validateTokenRequestDto.token());
        if(isTokenValid) return new ResponseEntity<>(true, HttpStatus.OK);
        else return new ResponseEntity<>(false, HttpStatus.BAD_GATEWAY);
    }

}