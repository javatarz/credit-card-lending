package me.karun.bank.credit.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Credit Card Lending Platform API",
        version = "0.0.1-SNAPSHOT",
        description = "API for the Credit Card Lending Platform",
        contact = @Contact(
            name = "API Support",
            email = "support@example.com"
        ),
        license = @License(
            name = "Proprietary",
            url = "https://example.com/license"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development")
    }
)
public class OpenApiConfig {
}
