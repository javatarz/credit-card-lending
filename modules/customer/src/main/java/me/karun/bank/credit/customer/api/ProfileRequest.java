package me.karun.bank.credit.customer.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import me.karun.bank.credit.customer.internal.validation.AdultAge;
import me.karun.bank.credit.customer.internal.validation.ValidSsn;

import java.time.LocalDate;

public record ProfileRequest(
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name must contain only letters, spaces, hyphens, and apostrophes")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name must contain only letters, spaces, hyphens, and apostrophes")
    String lastName,

    @NotNull(message = "Date of birth is required")
    @AdultAge
    LocalDate dateOfBirth,

    @NotBlank(message = "SSN is required")
    @ValidSsn
    String ssn,

    @NotNull(message = "Address is required")
    @Valid
    AddressDto address,

    String phone
) {}
