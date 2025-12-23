# Domain Documentation

This directory contains business rules and domain logic for each bounded context.

## Why Read This?

Domain docs explain **what** the business rules are and **why** they exist. They're essential for understanding the problem space before writing code.

**Prerequisites:** [overview.md](../overview.md), [glossary.md](../glossary.md)
**Related:** [modules/](../modules/) for technical implementation details

## Existing Domains

| Domain | Description |
|--------|-------------|
| [customer.md](customer.md) | Customer identity, registration, verification |

## Template for New Domain Docs

When creating a new domain doc, use this structure:

```markdown
# [Domain Name] Domain

## What is a [Entity]?

Brief definition of the core entity in business terms.

## [Entity] Lifecycle

State diagram showing valid transitions:
```
STATE_A → STATE_B → STATE_C
```

### States

| State | Description | Next Actions |
|-------|-------------|--------------|
| `STATE_A` | What this state means | What can happen next |

## [Entity] Data

### Core Fields

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| id | UUID | Auto | Primary identifier |

## Business Rules

### [Rule Category]
- Rule 1
- Rule 2

## Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `EntityCreatedEvent` | When created | Which modules listen |

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/...` | What it does |

## Data Storage

- **Schema**: `schema_name`
- **Tables**: `table1`, `table2`

## Related

- **Module**: Link to module doc
- **Stories**: #N, #N
- **Epic**: #N
```

## When to Create a Domain Doc

Create a domain doc when:
- Introducing a new bounded context
- Business rules need documentation beyond code comments
- The domain has a lifecycle with multiple states
- Multiple modules will interact with this domain
