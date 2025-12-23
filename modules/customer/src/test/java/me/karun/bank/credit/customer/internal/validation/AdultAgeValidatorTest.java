package me.karun.bank.credit.customer.internal.validation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AdultAgeValidatorTest {

    private final AdultAgeValidator validator = new AdultAgeValidator();

    @Test
    void shouldBeValid_when18YearsOldToday() {
        var dateOfBirth = LocalDate.now().minusYears(18);

        var result = validator.isValid(dateOfBirth, null);

        assertThat(result).isTrue();
    }

    @Test
    void shouldBeValid_whenOver18YearsOld() {
        var dateOfBirth = LocalDate.of(1990, 5, 15);

        var result = validator.isValid(dateOfBirth, null);

        assertThat(result).isTrue();
    }

    @Test
    void shouldBeInvalid_when17YearsOld() {
        var dateOfBirth = LocalDate.now().minusYears(17);

        var result = validator.isValid(dateOfBirth, null);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeInvalid_whenFutureDate() {
        var dateOfBirth = LocalDate.now().plusDays(1);

        var result = validator.isValid(dateOfBirth, null);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeValid_whenNull() {
        var result = validator.isValid(null, null);

        assertThat(result).isTrue();
    }
}
