package me.karun.bank.credit.customer.api;

import java.time.Instant;
import java.util.UUID;

public record VerifyEmailResponse(
        UUID customerId,
        String email,
        String status,
        Instant verifiedAt
) {
}
