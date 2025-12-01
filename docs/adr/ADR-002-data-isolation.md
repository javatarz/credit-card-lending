# ADR-002: Data Isolation Strategy

## Status

Accepted

## Context

PCI-DSS compliance requires strict isolation of cardholder data (PAN, CVV, expiry dates). We also need comprehensive audit trails for regulatory compliance with fair lending laws (ECOA, FCRA) and financial record retention requirements.

Key requirements:
- PCI-DSS Level 1 compliance for card data
- Audit trails for all sensitive operations
- Clear data ownership per module
- 7-year retention for financial records

## Decision

Use separate PostgreSQL schemas within a single RDS instance, with different access controls per schema.

### Schema Structure

```
Database: credit_card_platform
├── schema: customer        # Customer PII (name, SSN, address)
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

### Access Control Matrix

| Schema | Modules with Access | Access Type |
|--------|---------------------|-------------|
| customer | customer | Full |
| application | application, decisioning | Full / Read |
| account | account | Full |
| card_vault | account (card service only) | Full |
| transaction | transaction | Full |
| billing | billing | Full |
| payment | payment | Full |
| fraud | fraud | Full |
| support | support | Full |
| audit | All modules | Append-only |

### Card Vault Isolation

The `card_vault` schema requires additional protection:
- Separate database user with restricted privileges
- Application-level encryption for PAN
- No direct query access from other modules
- Tokenization for PAN references outside vault
- Encryption at rest (RDS encryption)

### Audit Schema

The `audit` schema is append-only:
- No UPDATE or DELETE privileges
- All writes through audit service
- Tamper-evident logging
- Retention policy enforcement

## Consequences

### Positive

- PCI compliance achievable without full service extraction
- Clear data ownership per module
- Audit trail maintained for all sensitive operations
- Simplified backup and retention per data type
- Schema-level access control adds security layer

### Negative

- More complex database setup
- Schema migration coordination required
- Cross-schema queries need careful design
- Connection pool management per schema

### Implementation Notes

1. Use separate DataSource beans per schema in Spring
2. Implement tokenization service for PAN references
3. Set up audit logging interceptor for all writes
4. Configure RDS parameter groups for encryption
5. Implement schema-specific Flyway migrations
