package me.karun.bank.credit.customer.api;

import jakarta.validation.Valid;
import java.util.Optional;

public record ProfileUpdateRequest(
    Optional<@Valid AddressDto> address,
    Optional<String> phone
) {}
