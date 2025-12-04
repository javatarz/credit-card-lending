package me.karun.bank.credit.customer.internal.config;

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
}
