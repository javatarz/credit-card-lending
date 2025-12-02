package me.karun.bank.credit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "me.karun.bank.credit")
@EntityScan(basePackages = "me.karun.bank.credit")
@EnableJpaRepositories(basePackages = "me.karun.bank.credit")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
