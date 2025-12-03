package me.karun.bank.credit.customer.internal.service;

import me.karun.bank.credit.customer.api.CustomerService;
import me.karun.bank.credit.customer.api.RegistrationRequest;
import me.karun.bank.credit.customer.api.RegistrationResponse;
import me.karun.bank.credit.customer.internal.domain.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public class CustomerServiceImpl implements CustomerService {

    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        var normalizedEmail = request.email().toLowerCase();

        return new RegistrationResponse(
                UUID.randomUUID(),
                normalizedEmail,
                CustomerStatus.PENDING_VERIFICATION.name(),
                Instant.now()
        );
    }
}
