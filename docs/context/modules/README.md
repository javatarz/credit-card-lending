# Module Documentation

This directory contains technical implementation details for each module.

## Why Read This?

Module docs explain **how** a module is implemented: package structure, database schema, public APIs, and integration points. Read these when working within a specific module.

**Prerequisites:** [overview.md](../overview.md), corresponding [domain/](../domain/) doc
**Related:** [conventions.md](../conventions.md) for code patterns

## Existing Modules

| Module | Description | Status |
|--------|-------------|--------|
| [customer-module.md](customer-module.md) | Customer identity, registration, verification | Partial |

## Planned Modules

| Module | Description |
|--------|-------------|
| application | Credit application processing |
| decisioning | Credit decision engine |
| account | Account & card management |
| transaction | Transaction processing |
| billing | Statements & billing cycles |
| payment | Payment processing |
| fraud | Fraud detection (separate service) |
| support | Customer support cases |
| processor-sim | Card processor simulation |

## Template for New Module Docs

When creating a new module doc, use this structure:

```markdown
# [Module Name] Module

## Responsibility

One paragraph describing what this module owns and why it exists.

## Implementation Status

| Story | Status | Description |
|-------|--------|-------------|
| #N | ✅ Done | What was built |
| #N | Pending | What's planned |

## Package Structure

```
me.karun.bank.credit.[module]/
├── api/                    # Public API (interfaces, DTOs)
│   ├── [Module]Service.java
│   ├── [Request/Response DTOs]
│   └── [Custom exceptions]
├── internal/               # Private implementation
│   ├── config/
│   ├── domain/
│   ├── repository/
│   └── service/
└── web/                    # REST controllers (if applicable)
```

## Database Schema

**Schema name**: `[module_name]`

```sql
-- Key tables with column definitions
```

## Public API

### REST Endpoints

| Method | Path | Description | Status |
|--------|------|-------------|--------|
| POST | `/api/v1/...` | What it does | ✅/Planned |

### Service Interface

```java
public interface [Module]Service {
    // Method signatures with status
}
```

### Events Published

| Event | When | Payload |
|-------|------|---------|
| `[Event]` | Trigger condition | Key fields |

### Events Consumed

| Event | From Module | Handler |
|-------|-------------|---------|
| `[Event]` | Source module | What it does |

## Dependencies

### Depends On
- `shared:kernel` - What's used
- `[other module]` - Why

### Depended On By
- `[module]` - How it uses this module

## Integration Points

### Outbound
- **[System]**: What integration

### Inbound
- **API Gateway**: REST endpoints

## Security Considerations

- Key security measures for this module

## Related

- **Domain**: Link to domain doc
- **Epic**: #N
- **Stories**: #N, #N
```

## When to Create a Module Doc

Create a module doc when:
- Starting implementation of a new module
- The first story for a module is picked up
- Module boundaries need documentation for other teams
