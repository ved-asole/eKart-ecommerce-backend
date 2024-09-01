package com.vedasole.ekartecommercebackend.config;

import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.repository.UserRepo;
import com.vedasole.ekartecommercebackend.utility.AppConstant;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    private final String USER_NOT_FOUND_ERROR_MESSAGE = "User not found";
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    void shouldThrowExceptionWhenUserEmailNotFound() {
        String nonExistentEmail = "nonExistentEmail@example.com";
        when(userRepo.findByEmailIgnoreCase(nonExistentEmail)).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            applicationConfig.userDetailsService().loadUserByUsername(nonExistentEmail);
        });
        assertThat(thrown.getMessage()).isEqualTo(USER_NOT_FOUND_ERROR_MESSAGE);
    }
    
    @Test
    void shouldHandleCaseSensitivityWhenSearchingForUserByEmail() {
        String email = "testUser@example.com";
        String emailWithDifferentCase = "TESTUSER@EXAMPLE.COM";
        User user = new User(email, "password", AppConstant.Role.USER);
        when(userRepo.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(userRepo.findByEmailIgnoreCase(emailWithDifferentCase)).thenReturn(Optional.of(user));

        UserDetails loadedUser1 = applicationConfig.userDetailsService().loadUserByUsername(email);
        UserDetails loadedUser2 = applicationConfig.userDetailsService().loadUserByUsername(emailWithDifferentCase);

        assertThat(loadedUser1).isEqualTo(loadedUser2);
    }
    
    @Test
    void shouldThrowExceptionWhenUserEmailIsNull() {
        String nullEmail = null;
        when(userRepo.findByEmailIgnoreCase(nullEmail)).thenReturn(Optional.empty());
    
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            applicationConfig.userDetailsService().loadUserByUsername(nullEmail);
        });
        assertThat(thrown.getMessage()).isEqualTo(USER_NOT_FOUND_ERROR_MESSAGE);
    }
    
    @Test
    void shouldThrowExceptionWhenUserEmailIsEmpty() {
        String emptyEmail = "";
        when(userRepo.findByEmailIgnoreCase(emptyEmail)).thenReturn(Optional.empty());
    
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            applicationConfig.userDetailsService().loadUserByUsername(emptyEmail);
        });
        assertThat(thrown.getMessage()).isEqualTo(USER_NOT_FOUND_ERROR_MESSAGE);
    }
    
    @Test
    void shouldThrowExceptionWhenUserEmailIsOnlyWhitespace() {
        String whitespaceEmail = " ";
        when(userRepo.findByEmailIgnoreCase(whitespaceEmail)).thenReturn(Optional.empty());
    
        UsernameNotFoundException thrown = assertThrows(
                UsernameNotFoundException.class,
                () -> applicationConfig.userDetailsService().loadUserByUsername(whitespaceEmail)
        );
        assertThat(thrown.getMessage()).isEqualTo(USER_NOT_FOUND_ERROR_MESSAGE);
    }
    
    @Test
    @Disabled("The password strength validation is not implemented yet")
    void shouldValidatePasswordStrengthWhenCreatingANewUser() {
        String weakPassword = "weak";
        String mediumPassword = "medium123";
        String strongPassword = "StrongP@ssw0rd";
    
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(weakPassword))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
    
        assertThatCode(() -> userDetailsService.loadUserByUsername(mediumPassword))
            .doesNotThrowAnyException();
    
        assertThatCode(() -> userDetailsService.loadUserByUsername(strongPassword))
            .doesNotThrowAnyException();
    }
    
    @Test
    void shouldVerifyPasswordEncoderIsCorrectlyConfigured() {
        PasswordEncoder passwordEncoder = applicationConfig.passwordEncoder();
    
        String plainTextPassword = "plainTextPassword";
        String encodedPassword = passwordEncoder.encode(plainTextPassword);
    
        assertThat(passwordEncoder.matches(plainTextPassword, encodedPassword)).isTrue();
    }
    
}