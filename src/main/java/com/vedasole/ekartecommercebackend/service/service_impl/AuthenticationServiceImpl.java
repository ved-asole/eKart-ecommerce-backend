package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.entity.PasswordResetToken;
import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.AuthenticationRequest;
import com.vedasole.ekartecommercebackend.payload.AuthenticationResponse;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;
import com.vedasole.ekartecommercebackend.repository.PasswordResetTokenRepo;
import com.vedasole.ekartecommercebackend.repository.UserRepo;
import com.vedasole.ekartecommercebackend.security.JwtService;
import com.vedasole.ekartecommercebackend.service.service_interface.AuthenticationService;
import com.vedasole.ekartecommercebackend.service.service_interface.CustomerService;
import com.vedasole.ekartecommercebackend.service.service_interface.EmailService;
import com.vedasole.ekartecommercebackend.service.service_interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class implements the AuthenticationService interface and provides authentication and token validation functionalities.
 *
 * @author Ved Asole
 * @version 1.0
 * @since 2024-09-03
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${frontendDomainUrl:http://localhost:5173}")
    private String frontendDomainUrl;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final UserRepo userRepo;
    private final EmailService emailService;

    /**
     * Authenticates a user using their email and password.
     *
     * @param authRequest The authentication request containing the user's email and password.
     * @param request The HTTP request for obtaining the user's IP address.
     * @return An AuthenticationResponse object containing the JWT token, customer details, and shopping cart ID.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest authRequest, HttpServletRequest request) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );
        User savedUser = this.userService.getUserByEmail(authRequest.getEmail());
        CustomerDto customerDto = this.customerService.getCustomerByEmail(authRequest.getEmail());
        String jwtToken = this.jwtService.generateToken(savedUser);
        return new AuthenticationResponse(
                jwtToken,
                customerDto.getFirstName(),
                customerDto.getLastName(),
                customerDto.getCustomerId(),
                customerDto.getEmail(),
                customerDto.getRole(),
                customerDto.getShoppingCart().getCartId()
        );
    }

    /**
     * Authenticates a user using their JWT token.
     *
     * @param request The HTTP request containing the JWT token in the Authorization header.
     * @return True if the token is valid, false otherwise.
     */
    @Override
    public boolean authenticate(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return
                authorization != null &&
                authorization.startsWith("Bearer ") &&
                isTokenValid(authorization.substring(7));
    }

    /**
     * Validates a JWT token.
     *
     * @param token The JWT token to validate.
     * @return True if the token is valid, false otherwise.
     */
    @Override
    public boolean isTokenValid(String token) {
        String username = jwtService.extractUsername(token);
        User user = userService.getUserByEmail(username);
        return jwtService.isTokenValid(token,user);
    }

    /**
     * Generates a password reset token for the user with the given email address.
     *
     * @param email The email address of the user for whom the password reset token is generated.
     */
    public void generatePasswordResetToken(String email) throws MessagingException {
        // Generate a token
        String token = UUID.randomUUID().toString();

        // Save the token with the user's email and an expiration time
        PasswordResetToken resetToken = new PasswordResetToken(token, email);
        passwordResetTokenRepo.save(resetToken);

        // Send email
        String resetUrl = frontendDomainUrl + "/reset-password?token=" + token;
        sendPasswordResetEmail(email, resetUrl);
    }

    /**
     * Resets the user's password using the provided token and new password.
     *
     * @param token The token for resetting the password.
     * @param newPassword The new password to be set for the user.
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepo.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("PasswordResetToken", "token", token));

        // Check if the token is valid and hasn't expired
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) return false;

        // Find the user by email and update the password
        User user = userRepo.findByEmailIgnoreCase(resetToken.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", resetToken.getEmail()));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Invalidate the token
        passwordResetTokenRepo.delete(resetToken);

        return true;
    }


    @Override
    @Transactional
    public boolean isResetTokenValid(String token) {
        return passwordResetTokenRepo.findByToken(token)
                .orElseThrow( () -> new ResourceNotFoundException("PasswordRestToken", "token", token))
                .getExpiryDate().isAfter(LocalDateTime.now());
    }

    public void sendPasswordResetEmail(String email, String resetUrl) throws MessagingException {
        try {
            Context context = new Context();
            context.setVariable("resetUrl", resetUrl);
            emailService.sendMimeMessage(email, "Ekart: Password Reset Request", context, "resetPassword");
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}", email, e);
            throw new MessagingException("Failed to send password reset email", e);
        }
    }

}