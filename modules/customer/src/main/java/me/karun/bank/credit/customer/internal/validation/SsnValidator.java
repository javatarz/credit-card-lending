package me.karun.bank.credit.customer.internal.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SsnValidator implements ConstraintValidator<ValidSsn, String> {

    private static final Pattern SSN_PATTERN = Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$");
    private static final String ALL_ZEROS = "000-00-0000";

    @Override
    public boolean isValid(String ssn, ConstraintValidatorContext context) {
        if (ssn == null) {
            return true; // @NotBlank handles null check
        }

        if (!SSN_PATTERN.matcher(ssn).matches()) {
            return false;
        }

        if (ALL_ZEROS.equals(ssn)) {
            if (context != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("SSN cannot be all zeros")
                    .addConstraintViolation();
            }
            return false;
        }

        return true;
    }
}
