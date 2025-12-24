# Customer Domain

## Why Read This?

Business rules for customer registration, verification, and profile management. Read before implementing customer-related features.

**Prerequisites:** [glossary.md](../glossary.md)
**Related:** [modules/customer-module.md](../modules/customer-module.md) for implementation details

---

## What is a Customer?

A Customer is an individual who has registered with the platform. A customer may or may not have a credit card account - registration is the first step before applying for credit.

## Customer Lifecycle

```
REGISTERED → PENDING_VERIFICATION → VERIFIED → PROFILE_COMPLETE
     │                │                           │
     │                │                           └─→ Can apply for credit
     │                └─→ Can resend verification
     └─→ Initial state after registration
```

### States

| State | Description | Next Actions |
|-------|-------------|--------------|
| `PENDING_VERIFICATION` | Registered but email not verified | Verify email, resend verification |
| `VERIFIED` | Email verified, profile incomplete | Complete profile |
| `PROFILE_COMPLETE` | Ready to apply for credit | Submit credit application |

## Customer Data

### Core Identity (Registration)

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| customerId | UUID | Auto | Primary identifier |
| email | String | Yes | Unique, validated format |
| passwordHash | String | Yes | BCrypt, strength 12 |
| status | Enum | Auto | Lifecycle state |
| createdAt | Timestamp | Auto | Registration time |
| verifiedAt | Timestamp | No | Email verification time |

### Profile (Post-Verification)

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| firstName | String | Yes | Legal first name |
| lastName | String | Yes | Legal last name |
| dateOfBirth | Date | Yes | Must be 18+ |
| ssn | String | Yes | Encrypted, format validated |
| address | Object | Yes | Street, city, state, zip |
| phone | String | No | Optional in MVP |

## Business Rules

### Registration
- Email must be unique (case-insensitive)
- Password requirements: 8+ chars, uppercase, lowercase, number, special char
- Registration triggers async email verification

### Email Verification
- Verification token valid for 24 hours
- Can resend verification (rate limited)
- Token is single-use

### Profile Completion
- Required before credit application
- SSN format validation (NNN-NN-NNNN)
- SSN encrypted at rest
- Age must be 18+ at time of application

### Profile Management
- **Immutable fields** (cannot change after profile completion): firstName, lastName, dateOfBirth, ssn, email
- **Mutable fields** (can be updated): address, phone
- Attempts to update immutable fields rejected (400 Bad Request)
- All profile changes recorded in audit log (field-level tracking)
- PATCH semantics supported for partial updates

## Audit Logging

Profile changes are recorded in `customer.profile_audit` table:

| Field | Description |
|-------|-------------|
| customerId | Who's profile changed |
| fieldName | Which field changed (e.g., "address.street", "phone") |
| oldValue | Previous value (SSN masked) |
| newValue | New value (SSN masked) |
| changedAt | When change occurred |
| changedBy | Who made the change (customerId) |

**Audit rules:**
- All mutable field changes recorded (address, phone)
- Changes recorded at field level (not entire profile)
- SSN never stored in audit log (masked if referenced)
- Provides compliance trail for customer data changes

## Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `CustomerRegisteredEvent` | Registration | Email service (send verification) |
| `CustomerVerifiedEvent` | Email verification | Audit log |
| `ProfileCompletedEvent` | Profile submission | Audit log, application module |

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/customers` | Create account |
| POST | `/api/v1/customers/verify-email` | Verify email token |
| POST | `/api/v1/customers/resend-verification` | Resend verification email |
| PUT | `/api/v1/customers/{customerId}/profile` | Complete/update profile (full) |
| GET | `/api/v1/customers/{customerId}/profile` | View profile |
| PATCH | `/api/v1/customers/{customerId}/profile` | Update profile (partial) |

## Data Storage

- **Schema**: `customer`
- **Tables**: `customers`, `customer_profiles`, `verification_tokens`
- **PII Handling**: SSN encrypted, all access audited

## Related

- **Module**: `modules/customer/` - see `docs/context/modules/customer-module.md`
- **Stories**: #21, #22, #23, #24
- **Epic**: #1 E01: Customer Onboarding
