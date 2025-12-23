package me.karun.bank.credit.customer.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressDto(
    @NotBlank(message = "Street address is required")
    String street,

    String unit,

    @NotBlank(message = "City is required")
    String city,

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "State must be 2 characters")
    @Pattern(regexp = "^(AL|AK|AZ|AR|CA|CO|CT|DE|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY|DC)$",
        message = "Invalid US state code")
    String state,

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Zip code must be in format NNNNN or NNNNN-NNNN")
    String zipCode
) {}
