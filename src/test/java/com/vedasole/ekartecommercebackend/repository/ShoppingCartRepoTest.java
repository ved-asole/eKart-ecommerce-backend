package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.utility.AppConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ShoppingCartRepoTest {

    @Autowired
    private ShoppingCartRepo underTest;

    @Autowired
    private CustomerRepo customerRepo;

    @BeforeEach
    void setUp() {
        // Initialize the database for testing

        //Add Customer
        Customer customer = new Customer(
                1L,
                "John",
                "Doe",
                "1234567890",
                "john@email.com",
                new User("john@email.com", "password", AppConstant.Role.USER),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Customer savedCustomer = customerRepo.save(customer);
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .cartId(5)
                .customer(savedCustomer)
                .total(0)
                .discount(0).build();
        ShoppingCart savedShoppingCart = underTest.save(shoppingCart);
        savedCustomer.setShoppingCart(savedShoppingCart);
        customerRepo.save(savedCustomer);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindShoppingCartByCustomerIdWhenExists() {
        // Given
        String customerEmail = "john@email.com";
        Optional<Customer> customerOptional = customerRepo.findByEmail(customerEmail);
        assertTrue(customerOptional.isPresent());
        long customerId = customerOptional.get().getCustomerId();

        // When
        Optional<ShoppingCart> shoppingCartOptional = underTest.findByCustomer_CustomerId(customerId);

        // Then
        assertTrue(shoppingCartOptional.isPresent());

        ShoppingCart expected = shoppingCartOptional.get();
        assertThat(expected).isNotNull();
        assertThat(expected.getCustomer().getCustomerId()).isEqualTo(customerId);
        assertThat(expected.getCustomer().getEmail()).isEqualTo(customerEmail);
        assertThat(expected.getCustomer()).isNotNull().isEqualTo(customerOptional.get());
    }

    @Test
    void shouldDeleteShoppingCartByCustomerId() {

        // Given
        String customerEmail = "john@email.com";
        Optional<Customer> customerOptional = customerRepo.findByEmail(customerEmail);
        assertTrue(customerOptional.isPresent());
        Customer customer = customerOptional.get();
        long cartId = customer.getShoppingCart().getCartId();

        // When
        Optional<ShoppingCart> expectedOptional = underTest.findById(cartId);
        assertTrue(expectedOptional.isPresent());
        underTest.deleteByCustomer_CustomerId(customer.getCustomerId());

        // Then
        Optional<ShoppingCart> outputOptional = underTest.findById(cartId);
        assertTrue(outputOptional.isEmpty());

        Optional<ShoppingCart> shoppingCartOptional = underTest.findById(cartId);
        assertTrue(shoppingCartOptional.isEmpty());
        assertThrows(NoSuchElementException.class, shoppingCartOptional::get);

    }
}