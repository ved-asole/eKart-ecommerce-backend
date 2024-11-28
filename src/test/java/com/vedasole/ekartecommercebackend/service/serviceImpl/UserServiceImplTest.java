package com.vedasole.ekartecommercebackend.service.serviceImpl;

import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.exception.APIException;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.repository.UserRepo;
import com.vedasole.ekartecommercebackend.service.service_impl.UserServiceImpl;
import com.vedasole.ekartecommercebackend.service.service_interface.UserService;
import com.vedasole.ekartecommercebackend.utility.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepo, passwordEncoder);
    }

    @Test
    void shouldCreateUserWhenEmailIsNotTaken() {
        // Given
        User user = new User(
                1,
                "john@email.com",
                "pass@123",
                AppConstant.Role.USER
        );

        // When
        userService.createUser(user);

        // Then
        ArgumentCaptor<User> userArgumentCapture = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userArgumentCapture.capture());

        User capturedUser = userArgumentCapture.getValue();
        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedUser.getPassword()).isNotEqualTo(passwordEncoder.encode(user.getPassword()));
    }

    @Test
    void shouldThrowErrorWhenEmailIsTaken() {
        // Given
        User user = new User(
                1,
                "john@email.com",
                "Doe",
                AppConstant.Role.USER
        );
        given(userRepo.findByEmailIgnoreCase(user.getEmail())).willReturn(java.util.Optional.of(user));

        // When

        // Then
        assertThatThrownBy( () -> userService.createUser(user))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("User with email " + user.getEmail() + " already exists");

        verify(userRepo, never()).save(any());
    }

    @Test
    void shouldHandleEmailUniquenessCaseInsensitively() {
        // Given
        User user1 = new User(1, "john@email.com", "pass@123", AppConstant.Role.USER);
        User user2 = new User(2, "JOHN@EMAIL.COM", "pass@123", AppConstant.Role.USER);
        given(userRepo.findByEmailIgnoreCase(user2.getEmail())).willReturn(java.util.Optional.of(user1));

        // Then
        assertThatThrownBy( () -> userService.createUser(user2))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("User with email " + user2.getEmail() + " already exists");

        verify(userRepo, never()).save(any());
    }

    @Test
    @Disabled("This test is disabled because the email format error is currently not being implemented in createUser method")
    void shouldThrowErrorWhenCreatingUserWithInvalidEmailFormat() {
        // Given
        User user = new User(
                1,
                "invalid_email",
                "pass@123",
                AppConstant.Role.USER
        );

        // Then
        assertThatThrownBy( () -> userService.createUser(user))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("Invalid email format");

        verify(userRepo, never()).save(any());
    }

    @Test
    void shouldUpdateUserWhenExists() {
        // Given
        User user = new User(
                1,
                "john@email.com",
                "pass@123",
                AppConstant.Role.USER
        );
        given(userRepo.findById(1L)).willReturn(java.util.Optional.of(user));

        // When
        user.setEmail("john2@email.com");
        given(userRepo.save(user)).willReturn(user);
        User updatedUser = userService.updateUser(user, 1L);

        // Then
        ArgumentCaptor<User> userArgumentCapture = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userArgumentCapture.capture());

        User capturedUser = userArgumentCapture.getValue();
        assertThat(capturedUser).isEqualTo(user);

        assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(updatedUser).isEqualTo(user);
        verify(userRepo).save(user);
    }

    @Test
    void shouldThrowErrorWhenUpdatingUserDoesNotExist() {
        // Given
        User user = new User(
                1,
                "john@email.com",
                "Doe",
                AppConstant.Role.USER
        );

        // Then
        assertThatThrownBy( () -> userService.updateUser(user, user.getUserId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with userId : " + user.getUserId());

        verify(userRepo, never()).save(any());
    }

    @Test
    void deleteUser() {
        // Given
        Long userId = 1L;

        // When
        boolean isDeleted = userService.deleteUser(userId);

        // Then
        verify(userRepo).deleteById(userId);
        assertThat(isDeleted).isTrue();
    }

    @Test
    void shouldThrowErrorWhileDeletingUserFails() {
        assertThatThrownBy( () -> userService.deleteUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid userId received to delete user : null");
    }

    @Test
    void shouldFindUserByEmailWhenExists() {

        // Given
        User user = new User(
                1,
                "invalid_email",
                "pass@123",
                AppConstant.Role.USER
        );
        given(userRepo.findByEmailIgnoreCase(user.getEmail())).willReturn(java.util.Optional.of(user));

        //When
        User userByEmail = userService.getUserByEmail(user.getEmail());

        //Then
        assertThat(userByEmail).isNotNull().isEqualTo(user);
        verify(userRepo).findByEmailIgnoreCase(user.getEmail());

    }

    @Test
    void shouldThrowErrorWhileFindingUserByEmailDoesNotExists() {

        // Given
        User user = new User(
                1,
                "john@email.com",
                "Doe",
                AppConstant.Role.USER
        );

        // When
        assertThatThrownBy( () ->  userService.getUserByEmail(user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with email : " + user.getEmail());

        // Then
        verify(userRepo).findByEmailIgnoreCase(user.getEmail());

    }

    @Test
    void shouldFetchUserById() {
        // Given
        User user = new User(
                1,
                "john@email.com",
                "pass@123",
                AppConstant.Role.USER
        );
        given(userRepo.findById(1L)).willReturn(java.util.Optional.of(user));

        // When
        userService.getUserById(1L);

        // Then
        verify(userRepo).findById(1L);

    }

    @Test
    void shouldThrowErrorWhileFetchingUserByIdWhenNotExists() {
        // Given
        User user = new User(
                1,
                "john@email.com",
                "pass@123",
                AppConstant.Role.USER
        );

        // When
        assertThatThrownBy( () -> userService.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with userId : " + user.getUserId());

        // Then
        verify(userRepo).findById(1L);

    }

    @Test
    void getAllUsers() {
        // When
        userService.getAllUsers();
        // Then
        verify(userRepo).findAll();
    }
}