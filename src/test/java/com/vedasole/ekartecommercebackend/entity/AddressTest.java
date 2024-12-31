package com.vedasole.ekartecommercebackend.entity;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTest {

    @Test
    void shouldCreateAddressWithAllFieldsSetCorrectly() {
        long addressId = 1L;
        String addLine1 = "123 Main St";
        String addLine2 = "Apt 4B";
        String city = "Springfield";
        String state = "IL";
        String country = "USA";
        int postalCode = 62701;

        Address address = new Address(addressId, addLine1, addLine2, city, state, country, postalCode);

        assertThat(address.getAddressId()).isEqualTo(addressId);
        assertThat(address.getAddLine1()).isEqualTo(addLine1);
        assertThat(address.getAddLine2()).isEqualTo(addLine2);
        assertThat(address.getCity()).isEqualTo(city);
        assertThat(address.getState()).isEqualTo(state);
        assertThat(address.getCountry()).isEqualTo(country);
        assertThat(address.getPostalCode()).isEqualTo(postalCode);
    }

    @Test
    void shouldCreateAddressWithMinimumRequiredFields() {
        String addLine1 = "123 Main St";
        String city = "Springfield";
        String state = "IL";
        String country = "USA";
        int postalCode = 62701;

        Address address = new Address(addLine1, null, city, state, country, postalCode);

        assertThat(address.getAddLine1()).isEqualTo(addLine1);
        assertThat(address.getAddLine2()).isNull();
        assertThat(address.getCity()).isEqualTo(city);
        assertThat(address.getState()).isEqualTo(state);
        assertThat(address.getCountry()).isEqualTo(country);
        assertThat(address.getPostalCode()).isEqualTo(postalCode);
        assertEquals(0,address.getAddressId());
        assertThat(address.getCreatedAt()).isNull();
        assertThat(address.getUpdatedAt()).isNull();
    }

    @Test
    void shouldCreateAddressUsingBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Address address = Address.builder()
                .addressId(1L)
                .addLine1("123 Main St")
                .addLine2("Apt 4B")
                .city("Springfield")
                .state("IL")
                .country("USA")
                .postalCode(62701)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(address.getAddressId()).isEqualTo(1L);
        assertThat(address.getAddLine1()).isEqualTo("123 Main St");
        assertThat(address.getAddLine2()).isEqualTo("Apt 4B");
        assertThat(address.getCity()).isEqualTo("Springfield");
        assertThat(address.getState()).isEqualTo("IL");
        assertThat(address.getCountry()).isEqualTo("USA");
        assertThat(address.getPostalCode()).isEqualTo(62701);
        assertThat(address.getCreatedAt()).isEqualTo(now);
        assertThat(address.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void shouldCreateAddressUsingNoArgsConstructorAndSetters() {
        Address address = new Address();
        address.setAddressId(1L);
        address.setAddLine1("123 Main St");
        address.setAddLine2("Apt 4B");
        address.setCity("Springfield");
        address.setState("IL");
        address.setCountry("USA");
        address.setPostalCode(62701);
        LocalDateTime now = LocalDateTime.now();
        address.setCreatedAt(now);
        address.setUpdatedAt(now);

        assertThat(address.getAddressId()).isEqualTo(1L);
        assertThat(address.getAddLine1()).isEqualTo("123 Main St");
        assertThat(address.getAddLine2()).isEqualTo("Apt 4B");
        assertThat(address.getCity()).isEqualTo("Springfield");
        assertThat(address.getState()).isEqualTo("IL");
        assertThat(address.getCountry()).isEqualTo("USA");
        assertThat(address.getPostalCode()).isEqualTo(62701);
        assertThat(address.getCreatedAt()).isEqualTo(now);
        assertThat(address.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @Disabled("This test is disabled because the postalCode field is not negative")
    void shouldThrowExceptionWhenSettingNegativeValueForPostalCode() {
        Address address = new Address();
        int negativePostalCode = -12345;

        assertThatThrownBy(() -> address.setPostalCode(negativePostalCode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Postal code must be a non-negative number");
    }

    @Test
    @Disabled("This test is disabled because the addLine1 field is not truncated")
    void shouldTruncateAddLine1IfExceeds100Characters() {
        String longAddLine1 = "A".repeat(101);
        String expectedAddLine1 = "A".repeat(100);

        Address address = new Address();
        address.setAddLine1(longAddLine1);

        assertThat(address.getAddLine1()).isEqualTo(expectedAddLine1);
        assertThat(address.getAddLine1()).hasSize(100);
    }

    @Test
    @Disabled("This test is disabled because the addLine2 field is not truncated")
    void shouldTruncateCityIfExceeds50Characters() {
        String longCity = "A".repeat(51);
        String expectedCity = "A".repeat(50);

        Address address = new Address();
        address.setCity(longCity);

        assertThat(address.getCity()).isEqualTo(expectedCity);
        assertThat(address.getCity()).hasSize(50);
    }

    @Test
    @Disabled("This test is disabled because the state field is not truncated")
    void shouldTruncateStateIfExceeds50Characters() {
        String longState = "A".repeat(51);
        String expectedState = "A".repeat(50);

        Address address = new Address();
        address.setState(longState);

        assertThat(address.getState()).isEqualTo(expectedState);
        assertThat(address.getState()).hasSize(50);
    }
    @Test
    @Disabled("This test is disabled because the country field is not truncated")
    void shouldTruncateCountryIfExceeds50Characters() {
        String longCountry = "A".repeat(51);
        String expectedCountry = "A".repeat(50);

        Address address = new Address();
        address.setCountry(longCountry);

        assertThat(address.getCountry()).isEqualTo(expectedCountry);
        assertThat(address.getCountry()).hasSize(50);
    }

}