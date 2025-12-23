package me.karun.bank.credit.customer.internal.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AdultAgeValidator implements ConstraintValidator<AdultAge, LocalDate> {

    private static final int MINIMUM_AGE = 18;

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return true; // @NotNull handles null check
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            return false; // Future date
        }

        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        return age >= MINIMUM_AGE;
    }
}
