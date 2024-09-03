package com.vedasole.ekartecommercebackend.service.serviceImpl;

import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.payload.AuthenticationRequest;
import com.vedasole.ekartecommercebackend.payload.AuthenticationResponse;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;
import com.vedasole.ekartecommercebackend.security.JwtService;
import com.vedasole.ekartecommercebackend.service.serviceInterface.AuthenticationService;
import com.vedasole.ekartecommercebackend.service.serviceInterface.CustomerService;
import com.vedasole.ekartecommercebackend.service.serviceInterface.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * This class implements the AuthenticationService interface and provides authentication and token validation functionalities.
 *
 * @author Ved Asole
 * @version 1.0
 * @since 2024-09-03
 */
@Service
@AllArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;

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
}
