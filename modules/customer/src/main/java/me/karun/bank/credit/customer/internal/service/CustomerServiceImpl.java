package me.karun.bank.credit.customer.internal.service;

import me.karun.bank.credit.customer.api.CustomerService;
import me.karun.bank.credit.customer.api.InvalidEmailException;
import me.karun.bank.credit.customer.api.RegistrationRequest;
import me.karun.bank.credit.customer.api.RegistrationResponse;
import me.karun.bank.credit.customer.api.WeakPasswordException;
import me.karun.bank.credit.customer.internal.domain.Customer;
import me.karun.bank.credit.customer.internal.domain.CustomerStatus;
import me.karun.bank.credit.customer.internal.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        validateEmail(request.email());
        validatePassword(request.password());

        var normalizedEmail = request.email().toLowerCase();
        var passwordHash = passwordEncoder.encode(request.password());

        var customer = new Customer(
                normalizedEmail,
                passwordHash,
                CustomerStatus.PENDING_VERIFICATION,
                Instant.now()
        );

        var savedCustomer = customerRepository.save(customer);

        return new RegistrationResponse(
                savedCustomer.getId(),
                savedCustomer.getEmail(),
                savedCustomer.getStatus().name(),
                savedCustomer.getCreatedAt()
        );
    }

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException(email);
        }
    }

    private void validatePassword(String password) {
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
