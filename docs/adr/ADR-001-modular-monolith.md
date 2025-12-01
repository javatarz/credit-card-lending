# ADR-001: Modular Monolith Architecture

## Status

Accepted

## Context

We need an architecture that balances development velocity with the ability to scale and isolate specific components. The team size is small, but certain modules (fraud ML) need independent deployment cycles.

Key considerations:
- Small initial team
- Need for clear module boundaries
- Fraud module requires independent deployment
- 500 RPS transaction throughput requirement
- Future scalability needs

## Decision

Adopt a modular monolith architecture with clear module boundaries, allowing future extraction of services.

### Module Structure

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

### Module Boundaries

Each module:
- Has its own package namespace
- Owns its database schema(s)
- Exposes a defined public API (interfaces)
- Communicates via events or public APIs (no direct database access)

### Boundary Enforcement

- Gradle multi-module build with explicit dependencies
- ArchUnit tests to prevent boundary violations
- API-first design for inter-module communication

## Consequences

### Positive

- Faster initial development compared to microservices
- Clear boundaries enable future service extraction
- Single deployment unit simplifies operations initially
- Easier debugging and tracing
- Lower infrastructure costs

### Negative

- Must actively enforce module boundaries (discipline required)
- Shared database can lead to coupling if not careful
- Scaling requires scaling entire application
- Technology choices are unified across modules

### Risks

- Module boundaries may erode over time without enforcement
- Performance issues in one module affect others
- Mitigation: ArchUnit tests, code reviews, and design documentation
