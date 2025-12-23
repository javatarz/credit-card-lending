package me.karun.bank.credit.customer.internal.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerStatusTest {

    @Test
    void shouldHaveProfileCompleteStatus() {
        var status = CustomerStatus.PROFILE_COMPLETE;

        assertThat(status).isNotNull();
        assertThat(status.name()).isEqualTo("PROFILE_COMPLETE");
    }
}
