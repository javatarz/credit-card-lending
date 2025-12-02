# Customer Domain

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

## Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `CustomerRegisteredEvent` | Registration | Email service (send verification) |
| `CustomerVerifiedEvent` | Email verification | Audit log |
| `ProfileCompletedEvent` | Profile submission | Audit log, application module |

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/customers/register` | Create account |
| POST | `/api/v1/customers/verify` | Verify email token |
| POST | `/api/v1/customers/resend-verification` | Resend verification email |
| GET | `/api/v1/customers/me/profile` | Get current profile |
| PUT | `/api/v1/customers/me/profile` | Update profile |

## Data Storage

- **Schema**: `customer`
- **Tables**: `customers`, `customer_profiles`, `verification_tokens`
- **PII Handling**: SSN encrypted, all access audited

## Related

- **Module**: `modules/customer/` - see `docs/context/modules/customer-module.md`
- **Stories**: #21, #22, #23, #24
- **Epic**: #1 E01: Customer Onboarding
