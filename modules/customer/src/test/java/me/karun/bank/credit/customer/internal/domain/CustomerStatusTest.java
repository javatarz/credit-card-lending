package me.karun.bank.credit.customer.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CustomerStatusTest {

  @Test
  void shouldHaveProfileCompleteStatus_whenReferenced() {
    var status = CustomerStatus.PROFILE_COMPLETE;

    assertThat(status).isNotNull();
    assertThat(status.name()).isEqualTo("PROFILE_COMPLETE");
  }
}
