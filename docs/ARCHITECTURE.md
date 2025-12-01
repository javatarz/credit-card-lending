# Architecture Overview

## System Context

We are building a **Credit Card Issuer System** that:
- Manages the full credit card application lifecycle
- Makes lending decisions and owns credit risk
- Integrates with an external processor for transaction handling
- Handles billing, payments, and account management
- Performs fraud detection using in-house ML

## Architecture Decision Records

Key architectural decisions are documented in individual ADRs:

| ADR | Title | Summary |
|-----|-------|---------|
| [ADR-001](./adr/ADR-001-modular-monolith.md) | Modular Monolith | Start with modular monolith, extractable boundaries |
| [ADR-002](./adr/ADR-002-data-isolation.md) | Data Isolation | Separate PostgreSQL schemas for PCI compliance |
| [ADR-003](./adr/ADR-003-technology-stack.md) | Technology Stack | Java 21, Spring Boot 3.2, PostgreSQL, Terraform |
| [ADR-004](./adr/ADR-004-fraud-module-extraction.md) | Fraud Module | Separate deployment for ML model serving |
| [ADR-005](./adr/ADR-005-processor-simulation.md) | Processor Simulation | Mock processor for Visa/MC behavior |
| [ADR-006](./adr/ADR-006-event-driven-communication.md) | Event Communication | Spring events internally, broker later |

## Module Structure

```
credit-card-platform/
├── modules/
│   ├── customer/          # Customer identity & profile
│   ├── application/       # Credit application processing
│   ├── decisioning/       # Credit decision engine
│   ├── account/           # Account & card management
│   ├── transaction/       # Transaction processing
│   ├── billing/           # Statements & billing
│   ├── payment/           # Payment processing
│   ├── fraud/             # Fraud detection (extractable)
│   ├── support/           # Customer support
│   └── processor-sim/     # Processor simulation
├── shared/
│   ├── kernel/            # Shared domain primitives
│   ├── infrastructure/    # Cross-cutting concerns
│   └── events/            # Internal event definitions
└── platform/
    └── api-gateway/       # API entry point
```

## Data Architecture

```
Database: credit_card_platform
├── schema: customer        # Customer PII
├── schema: application     # Application data
├── schema: account         # Account data
├── schema: card_vault      # PCI-sensitive card data (isolated)
├── schema: transaction     # Transaction records
├── schema: billing         # Billing & statements
├── schema: payment         # Payment records
├── schema: fraud           # Fraud scores & decisions
├── schema: support         # Support cases
└── schema: audit           # Immutable audit logs
```

## Non-Functional Requirements

### Performance Targets
| Metric | Target |
|--------|--------|
| New applications/day | 1,000 |
| Active cardholders | 1,000,000 |
| Transaction throughput | 500 RPS |
| Authorization latency | < 100ms p99 |
| API response time | < 200ms p95 |

### Compliance Requirements
- **PCI-DSS**: Level 1 compliance for card data handling
- **KYC/AML**: Identity verification and suspicious activity monitoring
- **Fair Lending**: ECOA/FCRA compliance for credit decisions
- **Data Retention**: 7-year retention for financial records

### Availability
- Target: 99.9% uptime
- RTO: 4 hours
- RPO: 1 hour

## Infrastructure Overview

```
┌─────────────────────────────────────────────────────────────┐
│                         AWS Cloud                           │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                    VPC                               │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │   │
│  │  │ Public      │  │ Private     │  │ Database    │  │   │
│  │  │ Subnet      │  │ Subnet      │  │ Subnet      │  │   │
│  │  │ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │  │   │
│  │  │ │   ALB   │ │  │ │ECS Tasks│ │  │ │   RDS   │ │  │   │
│  │  │ └─────────┘ │  │ └─────────┘ │  │ │PostgreSQL│ │  │   │
│  │  └─────────────┘  └─────────────┘  │ └─────────┘ │  │   │
│  │                                     └─────────────┘  │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## Open Questions

1. **Message Broker Selection**: When we need async processing at scale, Kafka vs SQS?
2. **Secrets Management**: AWS Secrets Manager vs HashiCorp Vault?
3. **Observability Stack**: CloudWatch vs Datadog vs Grafana?

These will be decided as we progress beyond MVP.

## Related Documents

- [Features & Epics](./FEATURES.md) - High-level feature breakdown
- [MVP Scope](./MVP.md) - MVP definition and scope
- [ADR Index](./adr/README.md) - All architecture decision records
