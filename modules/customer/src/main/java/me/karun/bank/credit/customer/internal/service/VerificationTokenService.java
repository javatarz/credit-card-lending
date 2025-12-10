package me.karun.bank.credit.customer.internal.service;

import me.karun.bank.credit.customer.api.CustomerRegisteredEvent;
import me.karun.bank.credit.customer.internal.domain.VerificationToken;
import me.karun.bank.credit.customer.internal.repository.VerificationTokenRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private static final int TOKEN_EXPIRY_HOURS = 24;

    private final VerificationTokenRepository tokenRepository;

    public VerificationTokenService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @EventListener
    public void onCustomerRegistered(CustomerRegisteredEvent event) {
        var rawToken = UUID.randomUUID().toString();
        var tokenHash = hashToken(rawToken);
        var expiresAt = Instant.now().plus(TOKEN_EXPIRY_HOURS, ChronoUnit.HOURS);

        var verificationToken = new VerificationToken(event.customerId(), tokenHash, expiresAt);
        tokenRepository.save(verificationToken);
    }

    private String hashToken(String rawToken) {
        return Integer.toHexString(rawToken.hashCode());
    }
}
