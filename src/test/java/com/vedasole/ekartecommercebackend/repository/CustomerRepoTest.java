package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.entity.User;
import com.vedasole.ekartecommercebackend.utility.AppConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class CustomerRepoTest {

    @Autowired
    private CustomerRepo underTest;

    @BeforeEach
    void setUp() {
        //given
        String email = "johndoe@email.com";
        new ShoppingCart();
        Customer customer = new Customer(
                1L,
                "John",
                "Doe",
                "1234567890",
                email,
                new User(email, "password", AppConstant.Role.USER),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        underTest.save(customer);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindCustomerWithEmailWhenExists() {

        //given
        String email = "johndoe@email.com";

        //when
        Optional<Customer> customerOptional = underTest.findByEmail(email);

        //then
        Assertions.assertTrue(customerOptional.isPresent());

        Customer expectedCustomer = customerOptional.get();
        assertThat(expectedCustomer).isNotNull();
        assertThat(expectedCustomer.getEmail()).isEqualTo(email);
        assertThat(expectedCustomer.getFirstName()).isEqualTo("John");
        assertThat(expectedCustomer.getLastName()).isEqualTo("Doe");

    }

    @Test
    void shouldNotFindCustomerWithEmailAndThrowNoSuchElementException() {

        //given
        String email = "random@email.com";

        //when
        Optional<Customer> expected = underTest.findByEmail(email);

        //then
        Assertions.assertTrue(expected.isEmpty());

        assertThatThrownBy(expected::get).isInstanceOf(NoSuchElementException.class);
    }

}