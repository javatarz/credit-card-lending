package me.karun.bank.credit.customer.api;

import java.time.Instant;
import java.util.UUID;

public record RegistrationResponse(
        UUID customerId,
        String email,
        String status,
        Instant createdAt
) {
}
