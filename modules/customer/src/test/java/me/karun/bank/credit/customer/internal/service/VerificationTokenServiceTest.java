package me.karun.bank.credit.customer.internal.service;

import me.karun.bank.credit.customer.api.CustomerRegisteredEvent;
import me.karun.bank.credit.customer.internal.domain.VerificationToken;
import me.karun.bank.credit.customer.internal.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class VerificationTokenServiceTest {

    private VerificationTokenRepository tokenRepository;
    private VerificationTokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenRepository = mock(VerificationTokenRepository.class);
        tokenService = new VerificationTokenService(tokenRepository);
    }

    @Test
    void shouldCreateVerificationToken_whenCustomerRegisters() {
        var customerId = UUID.randomUUID();
        var event = new CustomerRegisteredEvent(customerId, "test@example.com", Instant.now());

        tokenService.onCustomerRegistered(event);

        var tokenCaptor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        var savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getCustomerId()).isEqualTo(customerId);
        assertThat(savedToken.getTokenHash()).isNotBlank();
    }

    @Test
    void shouldSetTokenExpiryTo24Hours() {
        var event = new CustomerRegisteredEvent(UUID.randomUUID(), "test@example.com", Instant.now());
        var beforeCreation = Instant.now();

        tokenService.onCustomerRegistered(event);

        var tokenCaptor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        var savedToken = tokenCaptor.getValue();
        var expectedExpiryMin = beforeCreation.plus(24, java.time.temporal.ChronoUnit.HOURS);
        var expectedExpiryMax = Instant.now().plus(24, java.time.temporal.ChronoUnit.HOURS);
        assertThat(savedToken.getExpiresAt()).isBetween(expectedExpiryMin, expectedExpiryMax);
    }
}
