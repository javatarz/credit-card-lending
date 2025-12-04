package me.karun.bank.credit.customer.api;

import java.time.Instant;
import java.util.UUID;

public record CustomerRegisteredEvent(
        UUID customerId,
        String email,
        Instant registeredAt
) {
}
