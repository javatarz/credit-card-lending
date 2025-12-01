# ADR-003: Technology Stack

## Status

Accepted (Updated)

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
| Language | Java | 25 LTS | Long-term support (Sep 2025), latest features |
| Framework | Spring Boot | 4.x | Spring Framework 7, modern features |
| Build Tool | Gradle | 9.x (Kotlin DSL) | Required for Spring Boot 4, multi-module support |
| Database | PostgreSQL | 16.x | Robust, excellent JSON support, RDS compatible |
| Migration | Liquibase | Latest | YAML changelogs, built-in rollback, multi-schema support |
| API Style | REST + OpenAPI | OpenAPI 3.1 | Industry standard, good tooling |

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
| IaC | Terraform/OpenTofu | 1.6+ | AWS support, open source |
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

### Java 25 Features to Leverage

- **Virtual Threads**: For high-throughput I/O operations (stable)
- **Pattern Matching**: Cleaner domain model code
- **Records**: Immutable DTOs and value objects
- **Sealed Classes**: Constrained type hierarchies
- **String Templates**: Cleaner string formatting
- **Structured Concurrency**: Better async handling

### Spring Boot 4 / Spring Framework 7 Features

- **HTTP Service Clients**: Declarative HTTP clients
- **API Versioning**: Built-in API versioning support
- **OpenTelemetry Integration**: Native observability
- **Kotlin Serialization**: Support for Kotlin projects
- **Problem Details**: RFC 7807 error responses
- **Jakarta EE 11**: Servlet 6.1, Validation 3.1

## Consequences

### Positive

- LTS versions ensure long-term support
- Spring Boot 4 provides cutting-edge features
- Liquibase provides rollback capability for compliance
- Testcontainers enables realistic integration testing
- Strong ecosystem and community support

### Negative

- Java/Spring has higher memory footprint than Go/Rust
- Spring Boot 4 is newer, community resources still building
- Verbose compared to Kotlin (but more team familiarity)

### Dependencies to Add

```kotlin
// build.gradle.kts (root)
plugins {
    java
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.tngtech.archunit:archunit-junit5")
}
```
