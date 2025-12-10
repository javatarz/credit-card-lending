package me.karun.bank.credit.customer.internal.service;

import me.karun.bank.credit.customer.api.*;
import me.karun.bank.credit.customer.internal.domain.Customer;
import me.karun.bank.credit.customer.internal.domain.CustomerStatus;
import me.karun.bank.credit.customer.internal.domain.VerificationToken;
import me.karun.bank.credit.customer.internal.repository.CustomerRepository;
import me.karun.bank.credit.customer.internal.repository.VerificationTokenRepository;
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
    private VerificationTokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private ApplicationEventPublisher eventPublisher;
    private CustomerServiceImpl service;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        tokenRepository = mock(VerificationTokenRepository.class);
        passwordEncoder = new BCryptPasswordEncoder(12);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new CustomerServiceImpl(customerRepository, tokenRepository, passwordEncoder, eventPublisher);
    }

    @Test
    void shouldCreateCustomerInPendingVerificationStatus_whenValidRegistration() {
        var request = new RegistrationRequest("user@example.com", "SecurePass123!");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> simulateJpaSave(invocation.getArgument(0)));

        var response = service.register(request);

        assertThat(response.customerId()).isNotNull();
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.status()).isEqualTo(CustomerStatus.PENDING_VERIFICATION.name());
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        var request = new RegistrationRequest("User@EXAMPLE.COM", "SecurePass123!");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> simulateJpaSave(invocation.getArgument(0)));

        var response = service.register(request);

        assertThat(response.email()).isEqualTo("user@example.com");
    }

    @Test
    void shouldPersistCustomerWithHashedPassword() {
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
    void shouldRejectRegistration_whenEmailFormatIsInvalid(String invalidEmail) {
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
    void shouldRejectRegistration_whenPasswordIsWeak(String weakPassword) {
        var request = new RegistrationRequest("user@example.com", weakPassword);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(WeakPasswordException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectRegistration_whenEmailIsEmpty(String emptyEmail) {
        var request = new RegistrationRequest(emptyEmail, "SecurePass123!");

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(InvalidEmailException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectRegistration_whenPasswordIsEmpty(String emptyPassword) {
        var request = new RegistrationRequest("user@example.com", emptyPassword);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(WeakPasswordException.class);
    }

    @Test
    void shouldRejectRegistration_whenEmailAlreadyExists() {
        var request = new RegistrationRequest("existing@example.com", "SecurePass123!");
        var existingCustomer = new Customer("existing@example.com", "hash", CustomerStatus.PENDING_VERIFICATION, Instant.now());
        when(customerRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingCustomer));

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void shouldAcceptEmailWithSpecialCharacters() {
        var request = new RegistrationRequest("user+tag@example.com", "SecurePass123!");
        when(customerRepository.findByEmail("user+tag@example.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> simulateJpaSave(invocation.getArgument(0)));

        var response = service.register(request);

        assertThat(response.email()).isEqualTo("user+tag@example.com");
    }

    @Test
    void shouldPublishCustomerRegisteredEvent_whenRegistrationSucceeds() {
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

    @Test
    void shouldVerifyCustomer_whenValidToken() {
        var customerId = UUID.randomUUID();
        var rawToken = "valid-token";
        var tokenHash = hashToken(rawToken);
        var customer = new Customer("user@example.com", "hash", CustomerStatus.PENDING_VERIFICATION, Instant.now());
        ReflectionTestUtils.setField(customer, "id", customerId);
        var token = new VerificationToken(customerId, tokenHash, Instant.now().plusSeconds(3600));
        when(tokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        var response = service.verifyEmail(new VerifyEmailRequest(rawToken));

        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.status()).isEqualTo("VERIFIED");
        assertThat(response.verifiedAt()).isNotNull();
    }

    @Test
    void shouldThrowTokenNotFoundException_whenTokenDoesNotExist() {
        var rawToken = "non-existent-token";
        when(tokenRepository.findByTokenHash(hashToken(rawToken))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verifyEmail(new VerifyEmailRequest(rawToken)))
                .isInstanceOf(TokenNotFoundException.class);
    }

    @Test
    void shouldThrowTokenExpiredException_whenTokenIsExpired() {
        var customerId = UUID.randomUUID();
        var rawToken = "expired-token";
        var tokenHash = hashToken(rawToken);
        var expiredToken = new VerificationToken(customerId, tokenHash, Instant.now().minusSeconds(3600));
        when(tokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> service.verifyEmail(new VerifyEmailRequest(rawToken)))
                .isInstanceOf(TokenExpiredException.class);
    }

    @Test
    void shouldReturnSuccess_whenCustomerAlreadyVerified() {
        var customerId = UUID.randomUUID();
        var rawToken = "valid-token";
        var tokenHash = hashToken(rawToken);
        var customer = new Customer("user@example.com", "hash", CustomerStatus.PENDING_VERIFICATION, Instant.now());
        ReflectionTestUtils.setField(customer, "id", customerId);
        customer.verify();
        var token = new VerificationToken(customerId, tokenHash, Instant.now().plusSeconds(3600));
        when(tokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        var response = service.verifyEmail(new VerifyEmailRequest(rawToken));

        assertThat(response.status()).isEqualTo("VERIFIED");
    }

    @Test
    void shouldDeleteToken_afterSuccessfulVerification() {
        var customerId = UUID.randomUUID();
        var rawToken = "valid-token";
        var tokenHash = hashToken(rawToken);
        var customer = new Customer("user@example.com", "hash", CustomerStatus.PENDING_VERIFICATION, Instant.now());
        ReflectionTestUtils.setField(customer, "id", customerId);
        var token = new VerificationToken(customerId, tokenHash, Instant.now().plusSeconds(3600));
        ReflectionTestUtils.setField(token, "id", UUID.randomUUID());
        when(tokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        service.verifyEmail(new VerifyEmailRequest(rawToken));

        verify(tokenRepository).delete(token);
    }

    private String hashToken(String rawToken) {
        return Integer.toHexString(rawToken.hashCode());
    }

    @Test
    void shouldResendVerification_whenCustomerExistsAndNotVerified() {
        var customerId = UUID.randomUUID();
        var customer = new Customer("user@example.com", "hash", CustomerStatus.PENDING_VERIFICATION, Instant.now());
        ReflectionTestUtils.setField(customer, "id", customerId);
        when(customerRepository.findByEmail("user@example.com")).thenReturn(Optional.of(customer));
        when(tokenRepository.countByCustomerIdAndCreatedAtAfter(eq(customerId), any(Instant.class))).thenReturn(0L);

        var response = service.resendVerification(new ResendVerificationRequest("user@example.com"));

        assertThat(response.message()).isEqualTo("Verification email sent if account exists");
        verify(tokenRepository).save(any(VerificationToken.class));
    }

    @Test
    void shouldReturnSuccess_whenEmailDoesNotExist() {
        when(customerRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        var response = service.resendVerification(new ResendVerificationRequest("unknown@example.com"));

        assertThat(response.message()).isEqualTo("Verification email sent if account exists");
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void shouldThrowRateLimitExceeded_whenTooManyResendRequests() {
        var customerId = UUID.randomUUID();
        var customer = new Customer("user@example.com", "hash", CustomerStatus.PENDING_VERIFICATION, Instant.now());
        ReflectionTestUtils.setField(customer, "id", customerId);
        when(customerRepository.findByEmail("user@example.com")).thenReturn(Optional.of(customer));
        when(tokenRepository.countByCustomerIdAndCreatedAtAfter(eq(customerId), any(Instant.class))).thenReturn(3L);

        assertThatThrownBy(() -> service.resendVerification(new ResendVerificationRequest("user@example.com")))
                .isInstanceOf(RateLimitExceededException.class);
    }

    @Test
    void shouldNotResend_whenCustomerAlreadyVerified() {
        var customerId = UUID.randomUUID();
        var customer = new Customer("user@example.com", "hash", CustomerStatus.PENDING_VERIFICATION, Instant.now());
        ReflectionTestUtils.setField(customer, "id", customerId);
        customer.verify();
        when(customerRepository.findByEmail("user@example.com")).thenReturn(Optional.of(customer));

        var response = service.resendVerification(new ResendVerificationRequest("user@example.com"));

        assertThat(response.message()).isEqualTo("Verification email sent if account exists");
        verify(tokenRepository, never()).save(any());
    }
}
