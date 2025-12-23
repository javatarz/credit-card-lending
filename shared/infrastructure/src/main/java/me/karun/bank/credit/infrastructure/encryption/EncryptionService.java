package me.karun.bank.credit.infrastructure.encryption;

public interface EncryptionService {
    String encrypt(String plaintext);
    String decrypt(String ciphertext);
}
