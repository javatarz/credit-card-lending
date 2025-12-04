package me.karun.bank.credit.customer.internal.service;

import me.karun.bank.credit.customer.api.CustomerRegisteredEvent;
import me.karun.bank.credit.customer.api.RegistrationRequest;
import me.karun.bank.credit.customer.internal.domain.Customer;
import me.karun.bank.credit.customer.internal.domain.CustomerStatus;
import me.karun.bank.credit.customer.internal.repository.CustomerRepository;
import me.karun.bank.credit.customer.api.EmailAlreadyExistsException;
import me.karun.bank.credit.customer.api.InvalidEmailException;
import me.karun.bank.credit.customer.api.WeakPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;
    private ApplicationEventPublisher eventPublisher;
    private CustomerServiceImpl service;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        passwordEncoder = new BCryptPasswordEncoder(12);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new CustomerServiceImpl(customerRepository, passwordEncoder, eventPublisher);
    }

    @Test
    void should_create_customer_in_pending_verification_status_when_valid_registration() {
        var request = new RegistrationRequest("user@example.com", "SecurePass123!");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> simulateJpaSave(invocation.getArgument(0)));

        var response = service.register(request);

        assertThat(response.customerId()).isNotNull();
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.status()).isEqualTo(CustomerStatus.PENDING_VERIFICATION.name());
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void should_normalize_email_to_lowercase_when_registering() {
        var request = new RegistrationRequest("User@EXAMPLE.COM", "SecurePass123!");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> simulateJpaSave(invocation.getArgument(0)));

        var response = service.register(request);

        assertThat(response.email()).isEqualTo("user@example.com");
    }

    @Test
    void should_persist_customer_with_hashed_password_when_registering() {
        var request = new RegistrationRequest("user@example.com", "SecurePass123!");
        var customerCaptor = ArgumentCaptor.forClass(Customer.class);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> simulateJpaSave(invocation.getArgument(0)));

        service.register(request);

        verify(customerRepository).save(customerCaptor.capture());
        var savedCustomer = customerCaptor.getValue();
        assertThat(savedCustomer.getEmail()).isEqualTo("user@example.com");
        assertThat(savedCustomer.getPasswordHash()).isNotEqualTo("SecurePass123!");
        assertThat(passwordEncoder.matches("SecurePass123!", savedCustomer.getPasswordHash())).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "missing-at.com", "@nodomain.com", "spaces in@email.com"})
    void should_reject_registration_when_email_format_is_invalid(String invalidEmail) {
        var request = new RegistrationRequest(invalidEmail, "SecurePass123!");

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(InvalidEmailException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Short1!",
            "nouppercase123!",
            "NOLOWERCASE123!",
            "NoNumbers!!",
            "NoSpecial123"
    })
    void should_reject_registration_when_password_is_weak(String weakPassword) {
        var request = new RegistrationRequest("user@example.com", weakPassword);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(WeakPasswordException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void should_reject_registration_when_email_is_empty(String emptyEmail) {
        var request = new RegistrationRequest(emptyEmail, "SecurePass123!");

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(InvalidEmailException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void should_reject_registration_when_password_is_empty(String emptyPassword) {
        var request = new RegistrationRequest("user@example.com", emptyPassword);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(WeakPasswordException.class);
    }

    @Test
    void should_reject_registration_when_email_already_exists() {
        var request = new RegistrationRequest("existing@example.com", "SecurePass123!");
        var existingCustomer = new Customer("existing@example.com", "hash", CustomerStatus.PENDING_VERIFICATION, Instant.now());
        when(customerRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingCustomer));

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void should_accept_email_with_special_characters() {
        var request = new RegistrationRequest("user+tag@example.com", "SecurePass123!");
        when(customerRepository.findByEmail("user+tag@example.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> simulateJpaSave(invocation.getArgument(0)));

        var response = service.register(request);

        assertThat(response.email()).isEqualTo("user+tag@example.com");
    }

    @Test
    void should_publish_customer_registered_event_when_registration_succeeds() {
        var request = new RegistrationRequest("user@example.com", "SecurePass123!");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> simulateJpaSave(invocation.getArgument(0)));
        var eventCaptor = ArgumentCaptor.forClass(CustomerRegisteredEvent.class);

        var response = service.register(request);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        var event = eventCaptor.getValue();
        assertThat(event.customerId()).isEqualTo(response.customerId());
        assertThat(event.email()).isEqualTo("user@example.com");
        assertThat(event.registeredAt()).isNotNull();
    }

    private Customer simulateJpaSave(Customer customer) {
        ReflectionTestUtils.setField(customer, "id", UUID.randomUUID());
        return customer;
    }
}
