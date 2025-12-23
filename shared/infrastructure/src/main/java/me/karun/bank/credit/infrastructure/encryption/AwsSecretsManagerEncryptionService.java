package me.karun.bank.credit.infrastructure.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public class AwsSecretsManagerEncryptionService implements EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final SecretKey secretKey;

    public AwsSecretsManagerEncryptionService(String base64EncodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(base64EncodedKey);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // TODO #23: Replace with AWS Secrets Manager integration
    // For now using a configured key, will retrieve from AWS Secrets Manager later
    public AwsSecretsManagerEncryptionService(SecretsManagerClient secretsManagerClient, String secretName) {
        this.secretKey = retrieveKeyFromSecretsManager(secretsManagerClient, secretName);
    }

    @Override
    public String encrypt(String plaintext) {
        try {
            byte[] iv = generateIv();

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt data", e);
        }
    }

    @Override
    public String decrypt(String encryptedData) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedData);

            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] ciphertext = new byte[byteBuffer.remaining()];
            byteBuffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext);
        } catch (Exception e) {
            throw new EncryptionException("Failed to decrypt data", e);
        }
    }

    private byte[] generateIv() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private SecretKey retrieveKeyFromSecretsManager(SecretsManagerClient client, String secretName) {
        // TODO #23: Implement AWS Secrets Manager integration
        // For now, this method signature is here for future implementation
        throw new UnsupportedOperationException("AWS Secrets Manager integration not yet implemented");
    }

    // Placeholder for AWS SDK client type
    public static class SecretsManagerClient {
        // Placeholder - will be replaced with software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
    }
}
