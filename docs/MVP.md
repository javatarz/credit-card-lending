# MVP Scope Definition

## Overview

This document defines the Minimum Viable Product (MVP) for the Credit Card Lending Platform. The MVP represents the thinnest possible slice that delivers end-to-end value.

## MVP Goal

**Enable a customer to apply for a credit card, receive a decision, use the card for transactions, receive a statement, and make a payment.**

This covers the complete credit card lifecycle in its simplest form.

## MVP User Journey

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Register   │───▶│   Apply     │───▶│  Decision   │───▶│ Card Issued │
│  (E01)      │    │  (E02)      │    │  (E03)      │    │  (E05)      │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                                                               │
                                                               ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Payment   │◀───│  Statement  │◀───│   Posted    │◀───│ Transaction │
│   (E09)     │    │   (E08)     │    │   (E07)     │    │   (E06)     │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## MVP Epics

| Epic | Scope for MVP |
|------|---------------|
| E01: Customer Onboarding | Basic registration, simplified KYC |
| E02: Credit Application | Single product application form |
| E03: Credit Decisioning | Rules-based decision, no manual review |
| E04: Account Management | Basic account creation and status |
| E05: Card Issuance | Virtual card only |
| E06: Transaction Authorization | Basic auth with credit check |
| E07: Transaction Posting | Simple posting, no categorization |
| E08: Statement Generation | Basic statement, no PDF |
| E09: Payment Processing | One-time ACH payment (simulated) |
| E10: Application Fraud | Simple rules + basic ML score |
| E11: Transaction Fraud | Simple rules + basic ML score |
| E12: Processor Simulation | Basic auth/settle simulation |

---

## MVP Feature Details

### E01: Customer Onboarding (MVP)

**In Scope**:
- Customer registration (email, password)
- Basic profile (name, DOB, SSN, address)
- Email verification
- SSN format validation (simulated verification)

**Out of Scope**:
- Phone verification
- Document upload
- Advanced identity verification
- Biometric verification

**Stories**:
1. As a visitor, I can register with email and password
2. As a visitor, I can verify my email address
3. As a registered user, I can complete my profile
4. As a registered user, I can view and edit my profile

---

### E02: Credit Application (MVP)

**In Scope**:
- Single credit card product
- Application form (income, employment, housing)
- Application submission
- Application status tracking
- Application retrieval

**Out of Scope**:
- Multiple product selection
- Document upload
- Resume incomplete application
- Application expiry

**Stories**:
1. As a customer, I can start a credit card application
2. As a customer, I can submit my application
3. As a customer, I can view my application status
4. As a customer, I can view my application details

---

### E03: Credit Decisioning (MVP)

**In Scope**:
- Automated decision (no manual queue)
- Simple credit policy rules
- Credit limit assignment (tiered by score)
- Single APR tier
- Decision storage

**Out of Scope**:
- Credit bureau integration
- Manual review queue
- Multiple APR tiers
- Adverse action notices
- Decision explanation

**Decision Rules (MVP)**:
- Annual income >= $25,000
- No existing account
- Fraud score < threshold
- Age >= 18

**Credit Limits (MVP)**:
| Income Range | Credit Limit |
|--------------|--------------|
| $25K - $50K | $1,000 |
| $50K - $100K | $3,000 |
| $100K+ | $5,000 |

**Stories**:
1. As the system, I can automatically evaluate an application
2. As the system, I can assign a credit limit based on income
3. As the system, I can approve or decline an application
4. As a customer, I can view my decision

---

### E04: Account Management (MVP)

**In Scope**:
- Account creation on approval
- Account status (ACTIVE only for MVP)
- Balance tracking
- Available credit display

**Out of Scope**:
- Account suspension
- Account closure
- Account updates

**Stories**:
1. As the system, I can create an account when application is approved
2. As a customer, I can view my account details
3. As a customer, I can view my current balance
4. As a customer, I can view my available credit

---

### E05: Card Issuance (MVP)

**In Scope**:
- Virtual card generation
- Card number (PAN) generation
- CVV generation
- Expiry date (3 years from issue)
- View card details (masked)

**Out of Scope**:
- Physical card
- Card replacement
- Multiple cards
- Card artwork selection

**Stories**:
1. As the system, I can generate a virtual card on account creation
2. As a customer, I can view my card details (masked)
3. As a customer, I can reveal my full card number (authenticated)
4. As a customer, I can view my CVV (authenticated)

---

### E06: Transaction Authorization (MVP)

**In Scope**:
- Authorization request processing
- Available credit check
- Card active check
- Fraud score check
- Hold creation
- Authorization response

**Out of Scope**:
- Merchant category restrictions
- Geographic restrictions
- Partial authorization
- Authorization reversal

**Stories**:
1. As the processor, I can submit an authorization request
2. As the system, I can validate the card is active
3. As the system, I can check available credit
4. As the system, I can check fraud score
5. As the system, I can create a hold and approve
6. As the system, I can decline for insufficient credit
7. As the system, I can decline for suspected fraud

---

### E07: Transaction Posting (MVP)

**In Scope**:
- Settlement processing
- Hold to posted conversion
- Balance update
- Transaction history

**Out of Scope**:
- Transaction categorization
- Merchant name enrichment
- Transaction search/filter
- Transaction export

**Stories**:
1. As the processor, I can submit a settlement batch
2. As the system, I can post transactions and update balance
3. As a customer, I can view my transaction history

---

### E08: Statement Generation (MVP)

**In Scope**:
- Monthly statement cycle (fixed day)
- Transaction listing
- Balance summary
- Minimum payment calculation (2% or $25)
- Due date (25 days from statement)
- Statement retrieval

**Out of Scope**:
- Interest calculation (0% APR for MVP)
- Fee assessment
- PDF generation
- Statement notification

**Stories**:
1. As the system, I can generate monthly statements
2. As the system, I can calculate minimum payment
3. As a customer, I can view my current statement
4. As a customer, I can view past statements

---

### E09: Payment Processing (MVP)

**In Scope**:
- One-time payment submission
- ACH payment (simulated)
- Payment posting
- Balance update
- Payment confirmation
- Payment history

**Out of Scope**:
- Debit card payment
- Autopay
- Scheduled payments
- Payment reversal (NSF)

**Stories**:
1. As a customer, I can submit a payment
2. As a customer, I can specify payment amount
3. As the system, I can process and post the payment
4. As a customer, I can view my payment history
5. As a customer, I can view payment confirmation

---

### E10: Application Fraud Detection (MVP)

**In Scope**:
- Fraud score generation
- Basic rules (velocity check)
- Score threshold for auto-decline
- Fraud decision storage

**Out of Scope**:
- ML model (use simple heuristics)
- Device fingerprinting
- Manual fraud review
- Fraud case management

**Rules (MVP)**:
- Multiple applications from same SSN in 30 days → high risk
- Income > $500K → manual review flag
- Age < 21 with income > $100K → elevated risk

**Stories**:
1. As the system, I can calculate a fraud score for applications
2. As the system, I can apply velocity rules
3. As the system, I can flag high-risk applications

---

### E11: Transaction Fraud Detection (MVP)

**In Scope**:
- Real-time fraud scoring
- Basic rules
- Score threshold for decline
- Fraud decision logging

**Out of Scope**:
- ML model (use simple rules)
- Customer notification
- Fraud case management
- Block/allow lists

**Rules (MVP)**:
- Transaction > $1,000 → elevated score
- International merchant → elevated score
- High-risk MCC → elevated score
- 5+ transactions in 1 hour → elevated score

**Stories**:
1. As the system, I can calculate a fraud score for transactions
2. As the system, I can apply transaction rules
3. As the system, I can decline high-risk transactions

---

### E12: Processor Simulation (MVP)

**In Scope**:
- Authorization request/response
- Settlement batch submission
- Simple UI for testing
- Configurable decline rate

**Out of Scope**:
- Chargeback simulation
- Network-specific codes
- Bulk test data generation

**Stories**:
1. As a tester, I can submit authorization requests
2. As a tester, I can submit settlement batches
3. As a tester, I can configure response behavior

---

## Technical MVP Requirements

### API Endpoints (MVP)

```
# Customer
POST   /api/v1/customers/register
POST   /api/v1/customers/verify-email
GET    /api/v1/customers/profile
PUT    /api/v1/customers/profile

# Application
POST   /api/v1/applications
GET    /api/v1/applications/{id}
GET    /api/v1/applications/{id}/decision

# Account
GET    /api/v1/accounts/{id}
GET    /api/v1/accounts/{id}/balance

# Card
GET    /api/v1/accounts/{id}/card
POST   /api/v1/accounts/{id}/card/reveal

# Transactions
GET    /api/v1/accounts/{id}/transactions

# Statements
GET    /api/v1/accounts/{id}/statements
GET    /api/v1/accounts/{id}/statements/{statementId}

# Payments
POST   /api/v1/accounts/{id}/payments
GET    /api/v1/accounts/{id}/payments

# Processor Simulation (Internal)
POST   /api/internal/processor/authorize
POST   /api/internal/processor/settle
```

### Database Schema (MVP)

```sql
-- Core tables needed for MVP
customers
applications
application_decisions
accounts
cards (vault schema)
transactions
transaction_holds
statements
payments
fraud_scores
```

### Infrastructure (MVP)

- Single ECS service (modular monolith)
- Separate ECS service for fraud scoring (placeholder)
- RDS PostgreSQL (single instance, multiple schemas)
- ALB for API gateway
- Basic CloudWatch logging

---

## MVP Timeline Breakdown

### Phase 1: Foundation
- Project scaffolding
- Database setup
- Authentication
- E01: Customer Onboarding

### Phase 2: Application Flow
- E02: Credit Application
- E10: Application Fraud
- E03: Credit Decisioning

### Phase 3: Account & Card
- E04: Account Management
- E05: Card Issuance

### Phase 4: Transactions
- E12: Processor Simulation
- E11: Transaction Fraud
- E06: Transaction Authorization
- E07: Transaction Posting

### Phase 5: Billing & Payment
- E08: Statement Generation
- E09: Payment Processing

### Phase 6: Integration & Polish
- End-to-end testing
- API documentation
- Basic monitoring

---

## Success Criteria

MVP is complete when:
1. A user can register and complete onboarding
2. A user can submit a credit application
3. Application receives automated decision
4. Approved user gets a virtual card
5. User can make simulated transactions
6. Transactions post to account
7. Monthly statement generates
8. User can make a payment
9. All flows have basic fraud checks

---

## Out of Scope (Post-MVP)

- Physical cards
- Multiple card products
- Interest calculation
- Fee assessment
- Disputes
- Collections
- Rewards
- Mobile app
- Card controls
- Customer support portal
- Advanced fraud ML
- Notifications
