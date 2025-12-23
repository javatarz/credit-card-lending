package me.karun.bank.credit.customer.internal.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SsnValidatorTest {

    private final SsnValidator validator = new SsnValidator();

    @Test
    void shouldBeValid_whenCorrectFormat() {
        var result = validator.isValid("123-45-6789", null);

        assertThat(result).isTrue();
    }

    @Test
    void shouldBeInvalid_whenMissingDashes() {
        var result = validator.isValid("123456789", null);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeInvalid_whenContainsLetters() {
        var result = validator.isValid("12A-45-6789", null);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeInvalid_whenAllZeros() {
        var result = validator.isValid("000-00-0000", null);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeInvalid_whenWrongFormat() {
        var result = validator.isValid("1234-56-789", null);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeValid_whenNull() {
        var result = validator.isValid(null, null);

        assertThat(result).isTrue();
    }
}
