# ADR-003: Technology Stack

## Status

Accepted

## Context

We need to select a technology stack that supports:
- High-throughput transaction processing (500 RPS)
- Long-term maintainability
- Team familiarity and productivity
- Cloud-native deployment on AWS
- Compliance and security requirements

## Decision

### Core Stack

| Component | Choice | Version | Rationale |
|-----------|--------|---------|-----------|
| Language | Java | 21 LTS | Long-term support, virtual threads, modern features |
| Framework | Spring Boot | 3.2.x | Mature ecosystem, excellent documentation |
| Build Tool | Gradle | 8.x (Kotlin DSL) | Flexible, good multi-module support |
| Database | PostgreSQL | 15.x | Robust, excellent JSON support, RDS compatible |
| Migration | Flyway | Latest | Simple, reliable schema migrations |
| API Style | REST + OpenAPI | OpenAPI 3.0 | Industry standard, good tooling |

### Testing Stack

| Component | Choice | Rationale |
|-----------|--------|-----------|
| Unit Testing | JUnit 5 | Standard Java testing |
| Mocking | Mockito | Well-integrated with Spring |
| Integration | Testcontainers | Realistic database testing |
| API Testing | REST Assured | Fluent API testing |
| Architecture | ArchUnit | Enforce module boundaries |

### Infrastructure Stack

| Component | Choice | Rationale |
|-----------|--------|-----------|
| IaC | Terraform | 1.6+ | Team familiarity, AWS support |
| Container Runtime | AWS ECS Fargate | Serverless containers, reduced ops |
| Container Registry | AWS ECR | Native ECS integration |
| Load Balancer | AWS ALB | Layer 7, path-based routing |
| Database | AWS RDS PostgreSQL | Managed, Multi-AZ support |
| Secrets | AWS Secrets Manager | Native integration |
| Logging | CloudWatch Logs | Unified with AWS |

### Development Tools

| Purpose | Tool |
|---------|------|
| Code Style | Spotless + Google Java Format |
| Static Analysis | SpotBugs, PMD |
| Dependency Check | OWASP Dependency Check |
| API Docs | SpringDoc OpenAPI |
| CI/CD | GitHub Actions |

### Java 21 Features to Leverage

- **Virtual Threads**: For high-throughput I/O operations
- **Pattern Matching**: Cleaner domain model code
- **Records**: Immutable DTOs and value objects
- **Sealed Classes**: Constrained type hierarchies

### Spring Boot 3.2 Features

- **Native Compilation Ready**: GraalVM option for future
- **Observability**: Built-in Micrometer support
- **Problem Details**: RFC 7807 error responses
- **HTTP Interface Clients**: Declarative HTTP clients

## Consequences

### Positive

- LTS versions ensure long-term support
- Spring Boot 3.x provides modern Java features
- Testcontainers enables realistic integration testing
- Familiar stack reduces learning curve
- Strong ecosystem and community support

### Negative

- Java/Spring has higher memory footprint than Go/Rust
- Spring Boot startup time (mitigated by keeping modular)
- Verbose compared to Kotlin (but more team familiarity)

### Dependencies to Add

```kotlin
// build.gradle.kts (root)
plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.tngtech.archunit:archunit-junit5")
}
```
