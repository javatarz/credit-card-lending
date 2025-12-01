# High-Level Features & Epics

## Overview

This document outlines all high-level features (epics) for the Credit Card Lending Platform. Each epic represents a significant business capability that will be broken down into user stories.

## Epic Index

| ID | Epic | Module | MVP | Priority |
|----|------|--------|-----|----------|
| E01 | Customer Onboarding | customer | Yes | P0 |
| E02 | Credit Application | application | Yes | P0 |
| E03 | Credit Decisioning | decisioning | Yes | P0 |
| E04 | Account Management | account | Yes | P0 |
| E05 | Card Issuance | account | Yes | P0 |
| E06 | Transaction Authorization | transaction | Yes | P0 |
| E07 | Transaction Posting | transaction | Yes | P0 |
| E08 | Statement Generation | billing | Yes | P0 |
| E09 | Payment Processing | payment | Yes | P0 |
| E10 | Application Fraud Detection | fraud | Yes | P0 |
| E11 | Transaction Fraud Detection | fraud | Yes | P0 |
| E12 | Processor Simulation | processor-sim | Yes | P0 |
| E13 | Card Controls | account | No | P1 |
| E14 | Dispute Management | support | No | P1 |
| E15 | Customer Support Cases | support | No | P1 |
| E16 | Collections | billing | No | P2 |
| E17 | Account Closure | account | No | P1 |
| E18 | Rewards Program | account | No | P2 |
| E19 | Credit Limit Management | account | No | P1 |
| E20 | Notifications | infrastructure | No | P1 |

---

## Epic Details

### E01: Customer Onboarding

**Module**: customer
**MVP**: Yes
**Priority**: P0

**Description**: Enable new customers to register and establish their identity in the system. This is the entry point for all credit card applicants.

**Key Capabilities**:
- Customer registration with basic information
- Email/phone verification
- KYC data collection
- Identity verification (simulated for MVP)
- SSN validation
- Address verification

**Compliance Considerations**:
- KYC/AML requirements
- Data privacy (PII handling)
- Identity theft prevention

**Dependencies**: None (entry point)

**Success Metrics**:
- Registration completion rate
- KYC pass rate
- Time to complete onboarding

---

### E02: Credit Application

**Module**: application
**MVP**: Yes
**Priority**: P0

**Description**: Allow customers to apply for a credit card by submitting financial and personal information.

**Key Capabilities**:
- Application form submission
- Income and employment information capture
- Financial information collection
- Document upload (future)
- Application status tracking
- Application expiry handling
- Resume incomplete applications

**Application States**:
```
DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED/DECLINED/REFERRED → EXPIRED
```

**Compliance Considerations**:
- Fair lending disclosures
- Adverse action notice requirements
- Application data retention

**Dependencies**: E01 (Customer Onboarding)

**Success Metrics**:
- Application completion rate
- Time to decision
- Approval rate

---

### E03: Credit Decisioning

**Module**: decisioning
**MVP**: Yes
**Priority**: P0

**Description**: Automated credit decision engine that evaluates applications and determines approval, credit limit, and APR.

**Key Capabilities**:
- Credit policy rules engine
- Credit score integration (simulated)
- Debt-to-income calculation
- Credit limit assignment
- APR determination based on risk tier
- Manual review queue for edge cases
- Decision explanation generation

**Decision Outputs**:
- Approve with credit limit and APR
- Decline with reason codes
- Refer for manual review

**Compliance Considerations**:
- ECOA (Equal Credit Opportunity Act)
- FCRA (Fair Credit Reporting Act)
- Adverse action notices
- Decision audit trail

**Dependencies**: E02 (Credit Application), E10 (Application Fraud)

**Success Metrics**:
- Straight-through processing rate
- Decision accuracy
- Default rate by risk tier

---

### E04: Account Management

**Module**: account
**MVP**: Yes
**Priority**: P0

**Description**: Manage credit card accounts throughout their lifecycle from creation to closure.

**Key Capabilities**:
- Account creation post-approval
- Account status management
- Account information updates
- Account balance tracking
- Available credit calculation
- Account statements access

**Account States**:
```
PENDING_ACTIVATION → ACTIVE → SUSPENDED → CLOSED
```

**Dependencies**: E03 (Credit Decisioning)

**Success Metrics**:
- Account activation rate
- Account health metrics
- Customer retention

---

### E05: Card Issuance

**Module**: account
**MVP**: Yes
**Priority**: P0

**Description**: Issue virtual and physical credit cards to approved customers.

**Key Capabilities**:
- Virtual card generation (instant)
- Physical card request
- Card number generation (PAN)
- CVV generation
- Expiry date management
- Card replacement
- Multiple cards per account

**Security Considerations**:
- PAN stored in isolated vault
- CVV never stored after issuance
- Tokenization for display

**Dependencies**: E04 (Account Management)

**Success Metrics**:
- Virtual card instant issuance rate
- Physical card delivery success rate

---

### E06: Transaction Authorization

**Module**: transaction
**MVP**: Yes
**Priority**: P0

**Description**: Process authorization requests from the processor and make real-time approve/decline decisions.

**Key Capabilities**:
- Real-time authorization processing
- Available credit check
- Card status validation
- Merchant category handling
- Hold creation for approved auths
- Authorization reversal
- Partial authorization support

**Authorization Flow**:
```
Processor → Auth Request → Fraud Check → Credit Check → Response → Hold Created
```

**Performance Requirements**:
- < 100ms p99 latency
- 500 RPS throughput

**Dependencies**: E05 (Card Issuance), E11 (Transaction Fraud), E12 (Processor Sim)

**Success Metrics**:
- Authorization approval rate
- False decline rate
- Average response time

---

### E07: Transaction Posting

**Module**: transaction
**MVP**: Yes
**Priority**: P0

**Description**: Post settled transactions to accounts and manage the transaction lifecycle.

**Key Capabilities**:
- Settlement file processing
- Hold to posted conversion
- Transaction categorization
- Balance updates
- Transaction history
- Transaction search

**Transaction States**:
```
AUTHORIZED (Hold) → POSTED → DISPUTED → RESOLVED
```

**Dependencies**: E06 (Transaction Authorization)

**Success Metrics**:
- Settlement accuracy
- Posting latency

---

### E08: Statement Generation

**Module**: billing
**MVP**: Yes
**Priority**: P0

**Description**: Generate monthly billing statements with all transactions, fees, and payment information.

**Key Capabilities**:
- Monthly statement cycle management
- Transaction summarization
- Interest calculation
- Fee assessment
- Minimum payment calculation
- Due date assignment
- Statement PDF generation
- Statement history

**Statement Components**:
- Previous balance
- Payments and credits
- Purchases
- Cash advances
- Fees charged
- Interest charged
- New balance
- Minimum payment due
- Payment due date

**Compliance Considerations**:
- TILA (Truth in Lending Act) disclosures
- Minimum payment warning
- Late fee disclosure

**Dependencies**: E07 (Transaction Posting)

**Success Metrics**:
- Statement accuracy
- On-time delivery rate

---

### E09: Payment Processing

**Module**: payment
**MVP**: Yes
**Priority**: P0

**Description**: Accept and process customer payments toward their credit card balance.

**Key Capabilities**:
- One-time payment submission
- ACH payment processing (simulated)
- Debit card payment (simulated)
- Payment confirmation
- Payment posting to account
- Payment reversal (NSF handling)
- Autopay enrollment
- Payment history

**Payment Allocation**:
1. Fees
2. Interest
3. Principal (highest APR first)

**Dependencies**: E04 (Account Management)

**Success Metrics**:
- Payment success rate
- Autopay enrollment rate
- On-time payment rate

---

### E10: Application Fraud Detection

**Module**: fraud
**MVP**: Yes
**Priority**: P0

**Description**: Detect fraudulent credit card applications using ML-based scoring and rules.

**Key Capabilities**:
- Application fraud scoring
- Identity verification signals
- Velocity checks (multiple apps)
- Device fingerprinting (future)
- Fraud rules engine
- Manual review queue
- Fraud decision audit

**Fraud Signals**:
- Synthetic identity indicators
- Application velocity
- Data consistency checks
- Known fraud patterns

**ML Model**:
- Input: Application features
- Output: Fraud probability score
- Threshold-based decisions

**Dependencies**: E02 (Credit Application)

**Success Metrics**:
- Fraud detection rate
- False positive rate
- Fraud losses

---

### E11: Transaction Fraud Detection

**Module**: fraud
**MVP**: Yes
**Priority**: P0

**Description**: Real-time fraud detection for transaction authorizations.

**Key Capabilities**:
- Real-time transaction scoring
- Rules-based fraud detection
- ML model scoring
- Velocity checks
- Merchant risk assessment
- Geographic anomaly detection
- Transaction blocking

**Performance Requirements**:
- < 50ms scoring latency (part of auth flow)

**ML Model**:
- Input: Transaction features, customer history
- Output: Fraud probability score
- Real-time inference

**Dependencies**: E06 (Transaction Authorization)

**Success Metrics**:
- Fraud detection rate
- False positive rate
- Customer friction score

---

### E12: Processor Simulation

**Module**: processor-sim
**MVP**: Yes
**Priority**: P0

**Description**: Simulate card network processor behavior for testing and development.

**Key Capabilities**:
- Authorization request handling
- Configurable response rules
- Settlement file generation
- Network response codes (Visa/MC)
- Transaction simulation UI
- Bulk transaction generation

**Simulation Modes**:
- Always approve
- Always decline
- Rule-based (amount, MCC, etc.)
- Random with configurable rates

**Dependencies**: None

**Success Metrics**:
- Simulation realism
- Test coverage enabled

---

### E13: Card Controls

**Module**: account
**MVP**: No
**Priority**: P1

**Description**: Allow customers to control how their card can be used.

**Key Capabilities**:
- Temporary card freeze
- Transaction type controls (online, international)
- Spending limits by category
- Merchant blocking
- Real-time alerts

**Dependencies**: E05 (Card Issuance)

---

### E14: Dispute Management

**Module**: support
**MVP**: No
**Priority**: P1

**Description**: Handle customer disputes for unauthorized or incorrect transactions.

**Key Capabilities**:
- Dispute initiation
- Provisional credit
- Investigation workflow
- Chargeback processing
- Dispute resolution
- Customer communication

**Compliance Considerations**:
- Reg E / Reg Z timelines
- Provisional credit requirements

**Dependencies**: E07 (Transaction Posting)

---

### E15: Customer Support Cases

**Module**: support
**MVP**: No
**Priority**: P1

**Description**: General customer support case management.

**Key Capabilities**:
- Case creation
- Case categorization
- Assignment and routing
- SLA tracking
- Resolution tracking
- Customer communication history

**Dependencies**: E01 (Customer Onboarding)

---

### E16: Collections

**Module**: billing
**MVP**: No
**Priority**: P2

**Description**: Manage delinquent accounts and collection activities.

**Key Capabilities**:
- Delinquency tracking
- Collection queues
- Payment arrangement plans
- Collection letter generation
- Account charge-off
- Recovery tracking

**Compliance Considerations**:
- FDCPA requirements
- Collection communication rules

**Dependencies**: E09 (Payment Processing)

---

### E17: Account Closure

**Module**: account
**MVP**: No
**Priority**: P1

**Description**: Handle voluntary and involuntary account closures.

**Key Capabilities**:
- Customer-initiated closure
- Balance payoff calculation
- Card cancellation
- Account closure confirmation
- Involuntary closure (fraud, delinquency)
- Closure reason tracking

**Dependencies**: E04 (Account Management)

---

### E18: Rewards Program

**Module**: account
**MVP**: No
**Priority**: P2

**Description**: Points/cashback rewards program for card usage.

**Key Capabilities**:
- Points earning rules
- Points balance tracking
- Redemption options
- Bonus categories
- Points expiration

**Dependencies**: E07 (Transaction Posting)

---

### E19: Credit Limit Management

**Module**: account
**MVP**: No
**Priority**: P1

**Description**: Manage credit limit changes throughout account lifecycle.

**Key Capabilities**:
- Credit limit increase requests
- Proactive limit increases
- Limit decrease (risk-based)
- Temporary limit increases
- Limit change notifications

**Dependencies**: E04 (Account Management), E03 (Credit Decisioning)

---

### E20: Notifications

**Module**: infrastructure
**MVP**: No
**Priority**: P1

**Description**: Customer notification system for alerts and communications.

**Key Capabilities**:
- Email notifications
- SMS notifications
- Push notifications (future)
- Notification preferences
- Template management
- Delivery tracking

**Notification Types**:
- Transaction alerts
- Payment reminders
- Statement ready
- Fraud alerts
- Account updates

**Dependencies**: Cross-cutting, used by all modules

---

## Epic Dependency Graph

```
E01 Customer Onboarding
 └── E02 Credit Application
      ├── E10 Application Fraud ──┐
      └── E03 Credit Decisioning ←┘
           └── E04 Account Management
                ├── E05 Card Issuance
                │    ├── E13 Card Controls
                │    └── E06 Transaction Authorization ← E12 Processor Sim
                │         ├── E11 Transaction Fraud
                │         └── E07 Transaction Posting
                │              ├── E08 Statement Generation
                │              ├── E14 Dispute Management
                │              └── E18 Rewards Program
                ├── E09 Payment Processing
                │    └── E16 Collections
                ├── E17 Account Closure
                └── E19 Credit Limit Management

E15 Customer Support Cases ← E01
E20 Notifications (cross-cutting)
```

---

## Next Steps

1. Define MVP scope in detail (see MVP.md)
2. Break down MVP epics into user stories
3. Technical spike for project scaffolding
4. Begin implementation with E01 → E02 → E03 flow
