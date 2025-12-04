package me.karun.bank.credit.customer.internal.service;

import me.karun.bank.credit.customer.api.CustomerService;
import me.karun.bank.credit.customer.api.InvalidEmailException;
import me.karun.bank.credit.customer.api.RegistrationRequest;
import me.karun.bank.credit.customer.api.RegistrationResponse;
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
}
