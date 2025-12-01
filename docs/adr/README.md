# Architecture Decision Records

This directory contains the Architecture Decision Records (ADRs) for the Credit Card Lending Platform.

## Index

| ID | Title | Status | Date |
|----|-------|--------|------|
| [ADR-001](./ADR-001-modular-monolith.md) | Modular Monolith Architecture | Accepted | 2024-01 |
| [ADR-002](./ADR-002-data-isolation.md) | Data Isolation Strategy | Accepted | 2024-01 |
| [ADR-003](./ADR-003-technology-stack.md) | Technology Stack | Accepted | 2024-01 |
| [ADR-004](./ADR-004-fraud-module-extraction.md) | Fraud Module as Extractable Service | Accepted | 2024-01 |
| [ADR-005](./ADR-005-processor-simulation.md) | Processor Integration (Simulation) | Accepted | 2024-01 |
| [ADR-006](./ADR-006-event-driven-communication.md) | Event-Driven Communication (Internal) | Accepted | 2024-01 |

## ADR Template

When creating new ADRs, use the following template:

```markdown
# ADR-XXX: Title

## Status
Proposed | Accepted | Deprecated | Superseded

## Context
What is the issue that we're seeing that is motivating this decision?

## Decision
What is the change that we're proposing and/or doing?

## Consequences
What becomes easier or more difficult as a result of this decision?
```
