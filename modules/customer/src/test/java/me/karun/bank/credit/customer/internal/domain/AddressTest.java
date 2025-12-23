package me.karun.bank.credit.customer.internal.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    @Test
    void shouldCreateAddress_whenAllFieldsProvided() {
        var address = new Address("123 Main St", "Apt 4B", "New York", "NY", "10001");

        assertThat(address.getStreet()).isEqualTo("123 Main St");
        assertThat(address.getUnit()).isEqualTo("Apt 4B");
        assertThat(address.getCity()).isEqualTo("New York");
        assertThat(address.getState()).isEqualTo("NY");
        assertThat(address.getZipCode()).isEqualTo("10001");
    }

    @Test
    void shouldCreateAddress_whenUnitIsNull() {
        var address = new Address("123 Main St", null, "New York", "NY", "10001");

        assertThat(address.getUnit()).isNull();
    }
}
