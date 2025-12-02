# System Overview

## What is This?

A credit card lending platform that handles the complete lifecycle of credit card products: customer onboarding, credit applications, account management, transaction processing, billing, and payments.

## Architecture

**Modular Monolith** with clear module boundaries, designed for future service extraction.

```
credit-card-lending/
├── modules/           # Feature modules (auto-discovered)
│   ├── customer/      # Customer identity & profile
│   ├── application/   # Credit application processing
│   ├── decisioning/   # Credit decision engine
│   ├── account/       # Account & card management
│   ├── transaction/   # Transaction processing
│   ├── billing/       # Statements & billing
│   ├── payment/       # Payment processing
│   ├── fraud/         # Fraud detection (separate service)
│   ├── support/       # Customer support cases
│   └── processor-sim/ # Card processor simulation
├── shared/
│   ├── kernel/        # Shared domain primitives
│   ├── infrastructure/# Cross-cutting concerns
│   └── events/        # Internal event definitions
└── platform/
    └── api-gateway/   # API entry point
```

## Key Architectural Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Architecture | Modular monolith | Fast development, clear boundaries, extractable |
| Data isolation | Schema per module | PCI compliance, clear ownership |
| Fraud service | Separate deployment | Independent ML model lifecycle |
| Communication | Spring events (internal) | Loose coupling, future Kafka migration path |

## Module Boundaries

Each module:
- Has its own package namespace under `me.karun.bank.credit`
- Owns its database schema
- Exposes a defined public API (interfaces)
- Communicates via events or public APIs only
- Cannot directly access another module's database

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 25 LTS |
| Framework | Spring Boot 4.x (Spring Framework 7) |
| Database | PostgreSQL 16.x (RDS) |
| Build | Gradle 9.x (Kotlin DSL) |
| Migrations | Liquibase |
| Infrastructure | Terraform, AWS ECS Fargate |

## Compliance Context

- **PCI-DSS Level 1**: Card data isolation in `card_vault` schema with tokenization
- **ECOA/FCRA**: Audit trails for credit decisions, 7-year retention
- **Audit logging**: Append-only audit schema, all sensitive operations logged

## Performance Requirements

| Metric | Target |
|--------|--------|
| Transaction throughput | 500 RPS |
| Transaction fraud scoring | < 50ms p99 |
| Application fraud scoring | < 500ms p99 |
| Availability | 99.9% |

## Related Documentation

- **ADRs**: `docs/adr/` - Technical decision records
- **Domain details**: `docs/context/domain/` - Business rules per domain
- **Module details**: `docs/context/modules/` - Technical boundaries per module
- **Current state**: `docs/context/current-state.md` - What's built vs planned
