package com.vedasole.ekartecommercebackend.service.service_interface;

import com.vedasole.ekartecommercebackend.payload.AuthenticationRequest;
import com.vedasole.ekartecommercebackend.payload.AuthenticationResponse;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

/**
 * This interface defines the methods for authentication-related operations.
 */
public interface AuthenticationService {

    /**
     * Authenticates the user using the provided authentication request and HTTP request.
     *
     * @param authRequest The authentication request containing the user's credentials.
     * @param request The HTTP request containing additional information about the user's session.
     * @return An authentication response containing the user's authentication status and access token.
     */
    AuthenticationResponse authenticate(AuthenticationRequest authRequest, HttpServletRequest request);

    /**
     * Authenticates the user based on the HTTP request.
     *
     * @param request The HTTP request containing additional information about the user's session.
     * @return True if the user is authenticated, false otherwise.
     */
    boolean authenticate(HttpServletRequest request);

    /**
     * Validates the provided access token.
     *
     * @param token The access token to be validated.
     * @return True if the token is valid, false otherwise.
     */
    boolean isTokenValid(String token);

    void generatePasswordResetToken(String email) throws MessagingException;

    boolean resetPassword(String token, String newPassword);

    boolean isResetTokenValid(String token);

}