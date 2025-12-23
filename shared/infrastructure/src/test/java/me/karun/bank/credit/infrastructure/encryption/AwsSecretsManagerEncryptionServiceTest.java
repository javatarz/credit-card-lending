package me.karun.bank.credit.infrastructure.encryption;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AwsSecretsManagerEncryptionServiceTest {

    // 32-byte key for AES-256 (256 bits / 8 = 32 bytes)
    private static final String TEST_KEY_BASE64 = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="; // 32 bytes in base64

    @Test
    void shouldEncryptAndDecrypt_whenValidPlaintext() {
        var service = new AwsSecretsManagerEncryptionService(TEST_KEY_BASE64);

        var plaintext = "123-45-6789";
        var encrypted = service.encrypt(plaintext);
        var decrypted = service.decrypt(encrypted);

        assertThat(encrypted).isNotEqualTo(plaintext);
        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    void shouldProduceDifferentCiphertext_whenSamePlaintext() {
        var service = new AwsSecretsManagerEncryptionService(TEST_KEY_BASE64);

        var plaintext = "123-45-6789";
        var encrypted1 = service.encrypt(plaintext);
        var encrypted2 = service.encrypt(plaintext);

        assertThat(encrypted1).isNotEqualTo(encrypted2);
    }

    @Test
    void shouldThrowException_whenDecryptingInvalidCiphertext() {
        var service = new AwsSecretsManagerEncryptionService(TEST_KEY_BASE64);

        assertThatThrownBy(() -> service.decrypt("invalid-ciphertext"))
            .isInstanceOf(EncryptionException.class);
    }
}
