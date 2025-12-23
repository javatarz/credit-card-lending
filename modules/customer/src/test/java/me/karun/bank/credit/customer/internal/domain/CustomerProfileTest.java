package me.karun.bank.credit.customer.internal.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerProfileTest {

    @Test
    void shouldCreateProfile_whenAllFieldsProvided() {
        var customerId = UUID.randomUUID();
        var dateOfBirth = LocalDate.of(1990, 5, 15);
        var address = new Address("123 Main St", "Apt 4B", "New York", "NY", "10001");

        var profile = new CustomerProfile(
            customerId,
            "John",
            "Doe",
            dateOfBirth,
            "encrypted-ssn-value",
            "6789",
            address,
            "+1-555-123-4567"
        );

        assertThat(profile.getCustomerId()).isEqualTo(customerId);
        assertThat(profile.getFirstName()).isEqualTo("John");
        assertThat(profile.getLastName()).isEqualTo("Doe");
        assertThat(profile.getDateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(profile.getSsnEncrypted()).isEqualTo("encrypted-ssn-value");
        assertThat(profile.getSsnLastFour()).isEqualTo("6789");
        assertThat(profile.getAddress()).isEqualTo(address);
        assertThat(profile.getPhone()).isEqualTo("+1-555-123-4567");
    }

    @Test
    void shouldCreateProfile_whenPhoneIsNull() {
        var customerId = UUID.randomUUID();
        var dateOfBirth = LocalDate.of(1990, 5, 15);
        var address = new Address("123 Main St", null, "New York", "NY", "10001");

        var profile = new CustomerProfile(
            customerId,
            "John",
            "Doe",
            dateOfBirth,
            "encrypted-ssn-value",
            "6789",
            address,
            null
        );

        assertThat(profile.getPhone()).isNull();
    }
}
