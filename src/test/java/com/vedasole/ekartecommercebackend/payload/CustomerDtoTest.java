package com.vedasole.ekartecommercebackend.payload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateFirstNameLengthWhenLessThanMinimum() {
        // Given
        CustomerDto customerDto = CustomerDto.builder()
                .customerId(1)
                .firstName("A")
                .lastName("Doe")
                .email("johndoe@example.com")
                .password("password123")
                .phoneNumber("1234567890")
                .role(USER)
                .build();

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customerDto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("First name must be between minimum of 3 characters and maximum of 20 characters",
                violations.iterator().next().getMessage());
    }

@Test
void shouldValidateFirstNameLengthWhenMoreThanMaximum() {
    // Given
    CustomerDto customerDto = CustomerDto.builder()
            .customerId(1)
            .firstName("Abcdefghijklmnopqrstuvwxyz")
            .lastName("Doe")
            .email("johndoe@example.com")
            .password("password123")
            .phoneNumber("1234567890")
            .role(USER)
            .build();

    // When
    Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customerDto);

    // Then
    assertEquals(1, violations.size());
    assertEquals("First name must be between minimum of 3 characters and maximum of 20 characters",
            violations.iterator().next().getMessage());
}

@Test
void shouldValidateLastNameLengthWhenLessThanMinimum() {
    // Given
    CustomerDto customerDto = CustomerDto.builder()
            .customerId(1)
            .firstName("John")
            .lastName("A")
            .email("johndoe@example.com")
            .password("password123")
            .phoneNumber("1234567890")
            .role(USER)
            .build();

    // When
    Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customerDto);

    // Then
    assertEquals(1, violations.size());
    assertEquals("Last name must be between minimum of 3 characters and maximum of 20 characters",
            violations.iterator().next().getMessage());
}

@Test
void shouldValidateLastNameLengthWhenMoreThanMaximum() {
    // Given
    CustomerDto customerDto = CustomerDto.builder()
            .customerId(1)
            .firstName("John")
            .lastName("Abcdefghijklmnopqrstuvwxyz")
            .email("johndoe@example.com")
            .password("password123")
            .phoneNumber("1234567890")
            .role(USER)
            .build();

    // When
    Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customerDto);

    // Then
    assertEquals(1, violations.size());
    assertEquals("Last name must be between minimum of 3 characters and maximum of 20 characters",
            violations.iterator().next().getMessage());
}

@Test
void shouldValidateEmailFormatWhenNotValid() {
    // Given
    CustomerDto customerDto = CustomerDto.builder()
            .customerId(1)
            .firstName("John")
            .lastName("Doe")
            .email("johndoeexample.com")
            .password("password123")
            .phoneNumber("1234567890")
            .role(USER)
            .build();

    // When
    Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customerDto);

    // Then
    assertEquals(1, violations.size());
    assertEquals("Email address is not valid", violations.iterator().next().getMessage());
}

@Test
void shouldValidatePasswordLengthWhenLessThanMinimum() {
    // Given
    NewCustomerDto newCustomerDto = NewCustomerDto.builder()
            .customerId(1)
            .firstName("John")
            .lastName("Doe")
            .email("johndoe@example.com")
            .password("pw")
            .phoneNumber("1234567890")
            .role(USER)
            .build();

    // When
    Set<ConstraintViolation<NewCustomerDto>> violations = validator.validate(newCustomerDto);

    // Then
    assertEquals(1, violations.size());
    assertEquals("Password must be between minimum of 3 characters and maximum of 20 characters",
            violations.iterator().next().getMessage());
}

@Test
void shouldValidatePasswordLengthWhenMoreThanMaximum() {
    // Given
    NewCustomerDto newCustomerDto = NewCustomerDto.builder()
            .customerId(1)
            .firstName("John")
            .lastName("Doe")
            .email("johndoe@example.com")
            .password("password12345678901234567890")
            .phoneNumber("1234567890")
            .role(USER)
            .build();

    // When
    Set<ConstraintViolation<NewCustomerDto>> violations = validator.validate(newCustomerDto);

    // Then
    assertEquals(1, violations.size());
    assertEquals("Password must be between minimum of 3 characters and maximum of 20 characters",
            violations.iterator().next().getMessage());
}

@Test
void shouldValidatePhoneNumberWhenBlank() {
    // Given
    CustomerDto customerDto = CustomerDto.builder()
            .customerId(1)
            .firstName("John")
            .lastName("Doe")
            .email("johndoe@example.com")
            .password("password123")
            .phoneNumber("")
            .role(USER)
            .build();

    // When
    Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customerDto);

    // Then
    assertEquals(1, violations.size());
    assertEquals("Phone number must be a valid 10-digit number", violations.iterator().next().getMessage());

}

    @Test
    void shouldValidatePhoneNumberWhenNull() {
        // Given
        CustomerDto customerDto = CustomerDto.builder()
                .customerId(1)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .password("password123")
                .phoneNumber(null)
                .role(USER)
                .build();

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customerDto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Phone number is required", violations.iterator().next().getMessage());


    }

    @Test
    void shouldValidatePhoneNumberWhenWhitespace() {
        // Given
        CustomerDto customerDto = CustomerDto.builder()
                .customerId(1)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .password("password123")
                .phoneNumber(" ")
                .role(USER)
                .build();

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customerDto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Phone number must be a valid 10-digit number", violations.iterator().next().getMessage());

    }

    @Test
    void shouldValidatePhoneNumberWhenNotValid() {
        // Given
        CustomerDto customerDto = CustomerDto.builder()
                .customerId(1)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .password("password123")
                .phoneNumber("123456789")
                .role(USER)
                .build();

        // When
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customerDto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Phone number must be a valid 10-digit number", violations.iterator().next().getMessage());

    }

    @Test
    void shouldValidateRoleWhenNull() {
        // Given
        NewCustomerDto newCustomerDto = NewCustomerDto.builder()
                .customerId(1)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .password("password123")
                .phoneNumber("1234567890")
                .role(null)
                .build();

        // When
        Set<ConstraintViolation<NewCustomerDto>> violations = validator.validate(newCustomerDto);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Role is required", violations.iterator().next().getMessage());
    }
}