package me.karun.bank.credit.customer.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProfileResponse(
    UUID customerId,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String ssnLastFour,
    AddressDto address,
    String phone,
    String profileStatus,
    Instant updatedAt
) {}
