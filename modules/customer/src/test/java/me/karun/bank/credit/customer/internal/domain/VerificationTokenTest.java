package me.karun.bank.credit.customer.internal.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationTokenTest {

    @Test
    void shouldBeExpired_whenExpiresAtIsInThePast() {
        var expiredAt = Instant.now().minus(1, ChronoUnit.HOURS);
        var token = new VerificationToken(UUID.randomUUID(), "hashedToken", expiredAt);

        var result = token.isExpired();

        assertThat(result).isTrue();
    }

    @Test
    void shouldNotBeExpired_whenExpiresAtIsInTheFuture() {
        var expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);
        var token = new VerificationToken(UUID.randomUUID(), "hashedToken", expiresAt);

        var result = token.isExpired();

        assertThat(result).isFalse();
    }
}
