package me.karun.bank.credit.customer.internal.config;

import me.karun.bank.credit.infrastructure.encryption.AwsSecretsManagerEncryptionService;
import me.karun.bank.credit.infrastructure.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CustomerConfig {

    @Bean
    public PasswordEncoder passwordEncoder(@Value("${customer.bcrypt.strength:12}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    public EncryptionService encryptionService(@Value("${customer.encryption.key}") String encryptionKey) {
        // TODO #23: Integrate with AWS Secrets Manager after story #49 (auth) is complete
        return new AwsSecretsManagerEncryptionService(encryptionKey);
    }
}
