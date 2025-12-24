# Glossary

## Why Read This?

Reference for domain terminology. Use these terms consistently in code, documentation, and conversations to maintain a shared language (per DDD principles).

**Prerequisites:** [overview.md](overview.md)
**Related:** [domain/](domain/) for business rules

---

Domain terms and definitions for the credit card lending platform.

## Customer & Identity

| Term | Definition |
|------|------------|
| **Customer** | An individual who has registered with the platform. May or may not have an account. |
| **Profile** | Customer's personal information: name, address, contact details, SSN. |
| **Immutable Field** | Profile field that cannot be changed after initial submission (e.g., SSN, DOB, name, email). Protects against identity fraud. |
| **Audit Log** | Record of changes to customer data, tracking who changed what and when. Required for compliance and customer support. |
| **KYC** | Know Your Customer - identity verification process required before account opening. |
| **PII** | Personally Identifiable Information - SSN, DOB, address. Stored in `customer` schema. |

## Credit Application

| Term | Definition |
|------|------------|
| **Application** | A request for a credit card account. Contains requested limit and customer info. |
| **Underwriting** | Process of evaluating creditworthiness to determine approval and credit limit. |
| **Credit Score** | External score (e.g., FICO) used in decisioning. |
| **Fraud Score** | Internal score (0.0-1.0) indicating application fraud likelihood. |
| **Decision** | Outcome of underwriting: APPROVED, DECLINED, PENDING_REVIEW. |
| **Adverse Action** | Required notification when application is declined, stating reasons. |

## Account & Card

| Term | Definition |
|------|------------|
| **Account** | A credit card account with a credit limit. One customer can have multiple accounts. |
| **Credit Limit** | Maximum amount customer can borrow on the account. |
| **Available Credit** | Credit limit minus current balance. |
| **Card** | Physical or virtual card linked to an account. Account can have multiple cards. |
| **PAN** | Primary Account Number - the 16-digit card number. Stored in `card_vault` schema. |
| **Card Token** | Non-sensitive reference to a card used outside the vault. |
| **CVV** | Card Verification Value - 3-digit security code. Never stored after authorization. |

## Transactions

| Term | Definition |
|------|------------|
| **Authorization** | Real-time request to approve a transaction. Creates a hold on available credit. |
| **Hold** | Temporary reduction of available credit pending transaction settlement. |
| **Posting** | Finalizing a transaction to the account balance (usually T+1 or T+2). |
| **Settlement** | Batch process where posted transactions are finalized with the processor. |
| **Merchant** | Business where the transaction occurred. Identified by MCC (Merchant Category Code). |
| **MCC** | Merchant Category Code - 4-digit code classifying merchant type (e.g., 5411 = grocery). |

## Billing & Payments

| Term | Definition |
|------|------------|
| **Statement** | Monthly summary of transactions, balance, and minimum payment due. |
| **Statement Date** | Day of month when statement is generated. |
| **Due Date** | Date by which minimum payment must be received (typically 21-25 days after statement). |
| **Minimum Payment** | Smallest allowed payment, usually max(fixed amount, % of balance). |
| **Grace Period** | Days between statement and due date where no interest accrues if paid in full. |
| **APR** | Annual Percentage Rate - yearly interest rate on carried balances. |
| **Finance Charge** | Interest charged on unpaid balance. |

## Collections & Risk

| Term | Definition |
|------|------------|
| **Delinquency** | Account with payment past due. Measured in days: 30, 60, 90, 120+ DPD. |
| **DPD** | Days Past Due - how many days since payment was due. |
| **Charge-off** | Writing off debt as uncollectible (typically at 180 DPD). |
| **Recovery** | Collecting on charged-off debt. |
| **Risk Level** | LOW, MEDIUM, HIGH classification from fraud or credit models. |

## Technical Terms

| Term | Definition |
|------|------------|
| **intelligent Engineering (iE)** | A framework for leveraging AI throughout the entire Software Development Lifecycle. Rather than replacing the SDLC, iE applies AI assistance across all phases—research, analysis, design, build, test, deploy, and feedback—to reduce administrative overhead and empower teams to focus on strategy and creativity. |
| **Module** | Bounded context with its own schema, package namespace, and public API. |
| **Schema** | PostgreSQL schema isolating a module's data. |
| **Domain Event** | Business event published when state changes (e.g., `ApplicationSubmitted`). |
| **Aggregate** | DDD concept - cluster of entities treated as a unit for consistency. |
| **Card Vault** | Isolated schema and service for storing PAN data (PCI compliance). |

## Compliance Abbreviations

| Term | Definition |
|------|------------|
| **PCI-DSS** | Payment Card Industry Data Security Standard - rules for handling card data. |
| **ECOA** | Equal Credit Opportunity Act - prohibits discrimination in lending. |
| **FCRA** | Fair Credit Reporting Act - regulates use of credit information. |
| **Reg Z** | Truth in Lending Act implementation - disclosure requirements. |
