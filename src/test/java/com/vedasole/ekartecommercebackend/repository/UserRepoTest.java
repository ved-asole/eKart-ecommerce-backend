package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.utility.AppConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRepoTest {

    @Autowired
    private UserRepo underTest;

    @BeforeEach
    void setUp() {
        //given
        User user = new User(
                "john@email.com",
                "pass@123",
                AppConstant.Role.USER
        );
        underTest.save(user);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindUserByEmailWhenExists() {

        // Given
        String email = "john@email.com";

        // When
        Optional<User> userOptional = underTest.findByEmailIgnoreCase(email);
        boolean foundUser = userOptional.isPresent();

        // Then
        Assertions.assertTrue(foundUser);
        assertThat(userOptional.get()).isNotNull().hasFieldOrPropertyWithValue("email", email);
    }

    @Test
    void shouldNotFindUserByEmailAndThrowNoSuchElementException() {
        // Given
        String email = "random@email.com";

        // When
        Optional<User> userOptional = underTest.findByEmailIgnoreCase(email);

        // Then
        assertThatThrownBy(userOptional::get).isInstanceOf(NoSuchElementException.class);
    }
}