# External Integrations

## Overview

The platform integrates with external systems for card processing, credit bureau data, and notifications. In MVP, some integrations are simulated.

## Card Processor

### Purpose
Handles card transaction authorization, settlement, and card issuance requests.

### Integration Type
HTTP API (simulated in MVP via `processor-sim` module)

### Operations
| Operation | Description | Latency Target |
|-----------|-------------|----------------|
| Authorize | Real-time transaction approval | < 100ms |
| Capture | Finalize authorized transaction | Async |
| Refund | Reverse a posted transaction | Async |
| Issue Card | Request physical/virtual card | Async |

### MVP Approach
- `processor-sim` module simulates processor responses
- Configurable success/failure rates for testing
- Same API contract as real processor

## Credit Bureau

### Purpose
Provides credit scores and credit reports for underwriting decisions.

### Integration Type
HTTP API (simulated in MVP)

### Operations
| Operation | Description |
|-----------|-------------|
| Pull Credit | Retrieve credit score and report |
| Soft Pull | Inquiry that doesn't affect score |
| Hard Pull | Inquiry that affects score (for applications) |

### MVP Approach
- Simulated responses with configurable scores
- No real bureau integration in MVP

## Fraud Service

### Purpose
ML-based fraud scoring for applications and transactions.

### Integration Type
HTTP API (separate ECS service)

### Operations
| Operation | Description | Latency Target |
|-----------|-------------|----------------|
| Score Application | Fraud risk for credit applications | < 500ms |
| Score Transaction | Real-time transaction fraud check | < 50ms |

### Details
See ADR-004 for architecture. Deployed as separate service even in MVP.

## Email Service

### Purpose
Sends transactional emails (verification, statements, alerts).

### Integration Type
AWS SES (future) / Console logging (MVP)

### Operations
| Operation | Description |
|-----------|-------------|
| Send Verification | Email verification link |
| Send Statement | Monthly statement PDF |
| Send Alert | Transaction/security alerts |

### MVP Approach
- Emails logged to console
- Template structure in place for SES migration

## Audit & Logging

### Purpose
Compliance-required audit trail and operational logging.

### Integration Type
Internal (audit schema) + CloudWatch Logs

### What's Logged
- All authentication events
- All credit decisions with reasons
- All data access to PII
- All financial transactions

## Future Integrations (Post-MVP)

| System | Purpose | Status |
|--------|---------|--------|
| AWS SES | Email delivery | Planned |
| Real Credit Bureau | Credit data | Planned |
| Real Processor | Card network | Planned |
| AWS SNS | Push notifications | Planned |
| Plaid | Bank account verification | Considered |
