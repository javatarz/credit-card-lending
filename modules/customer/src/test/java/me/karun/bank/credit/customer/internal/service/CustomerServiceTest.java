package me.karun.bank.credit.customer.internal.service;

import me.karun.bank.credit.customer.api.RegistrationRequest;
import me.karun.bank.credit.customer.api.RegistrationResponse;
import me.karun.bank.credit.customer.api.CustomerService;
import me.karun.bank.credit.customer.internal.domain.CustomerStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerServiceTest {

    @Test
    void should_create_customer_in_pending_verification_status_when_valid_registration() {
        // Given
        var service = new CustomerServiceImpl();
        var request = new RegistrationRequest("user@example.com", "SecurePass123!");

        // When
        var response = service.register(request);

        // Then
        assertThat(response.customerId()).isNotNull();
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.status()).isEqualTo(CustomerStatus.PENDING_VERIFICATION.name());
        assertThat(response.createdAt()).isNotNull();
    }
}
