package me.karun.bank.credit.customer.internal.service;

import me.karun.bank.credit.customer.api.*;
import me.karun.bank.credit.customer.internal.domain.Customer;
import me.karun.bank.credit.customer.internal.domain.CustomerStatus;
import me.karun.bank.credit.customer.internal.domain.VerificationToken;
import me.karun.bank.credit.customer.internal.repository.CustomerRepository;
import me.karun.bank.credit.customer.internal.repository.VerificationTokenRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final int TOKEN_EXPIRY_HOURS = 24;
    private static final int MAX_RESEND_PER_HOUR = 3;

    private final CustomerRepository customerRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            VerificationTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher) {
        this.customerRepository = customerRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        validateEmail(request.email());
        validatePassword(request.password());

        var normalizedEmail = request.email().toLowerCase();
        checkEmailNotTaken(normalizedEmail);
        var passwordHash = passwordEncoder.encode(request.password());

        var customer = new Customer(
                normalizedEmail,
                passwordHash,
                CustomerStatus.PENDING_VERIFICATION,
                Instant.now()
        );

        var savedCustomer = customerRepository.save(customer);

        eventPublisher.publishEvent(new CustomerRegisteredEvent(
                savedCustomer.getId(),
                savedCustomer.getEmail(),
                savedCustomer.getCreatedAt()
        ));

        return new RegistrationResponse(
                savedCustomer.getId(),
                savedCustomer.getEmail(),
                savedCustomer.getStatus().name(),
                savedCustomer.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest request) {
        var tokenHash = hashToken(request.token());
        var token = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(TokenNotFoundException::new);

        if (token.isExpired()) {
            throw new TokenExpiredException();
        }

        var customer = customerRepository.findById(token.getCustomerId())
                .orElseThrow(TokenNotFoundException::new);

        if (!customer.isVerified()) {
            customer.verify();
            customerRepository.save(customer);
        }

        tokenRepository.delete(token);

        return new VerifyEmailResponse(
                customer.getId(),
                customer.getEmail(),
                customer.getStatus().name(),
                customer.getVerifiedAt()
        );
    }

    @Override
    @Transactional
    public ResendVerificationResponse resendVerification(ResendVerificationRequest request) {
        var normalizedEmail = request.email().toLowerCase();
        var customerOpt = customerRepository.findByEmail(normalizedEmail);

        if (customerOpt.isEmpty()) {
            return new ResendVerificationResponse("Verification email sent if account exists");
        }

        var customer = customerOpt.get();

        if (customer.isVerified()) {
            return new ResendVerificationResponse("Verification email sent if account exists");
        }

        var recentTokenCount = countRecentTokens(customer.getId());
        if (recentTokenCount >= MAX_RESEND_PER_HOUR) {
            throw new RateLimitExceededException();
        }

        createVerificationToken(customer.getId());

        return new ResendVerificationResponse("Verification email sent if account exists");
    }

    private void createVerificationToken(java.util.UUID customerId) {
        var rawToken = java.util.UUID.randomUUID().toString();
        var tokenHash = hashToken(rawToken);
        var expiresAt = Instant.now().plus(TOKEN_EXPIRY_HOURS, ChronoUnit.HOURS);
        var verificationToken = new VerificationToken(customerId, tokenHash, expiresAt);
        tokenRepository.save(verificationToken);
    }

    private long countRecentTokens(java.util.UUID customerId) {
        return tokenRepository.countByCustomerIdAndCreatedAtAfter(
                customerId,
                Instant.now().minus(1, ChronoUnit.HOURS)
        );
    }

    private String hashToken(String rawToken) {
        return Integer.toHexString(rawToken.hashCode());
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException(email);
        }
    }

    private void checkEmailNotTaken(String email) {
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new WeakPasswordException("Password is required");
        }
        if (password.length() < 8) {
            throw new WeakPasswordException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new WeakPasswordException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new WeakPasswordException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new WeakPasswordException("Password must contain at least one number");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new WeakPasswordException("Password must contain at least one special character");
        }
    }
}
