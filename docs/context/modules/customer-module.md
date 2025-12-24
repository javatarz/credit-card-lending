# Customer Module

## Why Read This?

Technical implementation details for the customer module: package structure, APIs, database schema, and integration points.

**Prerequisites:** [domain/customer.md](../domain/customer.md) for business rules
**Related:** [conventions.md](../conventions.md) for code patterns

---

## Responsibility

Owns customer identity, registration, verification, and profile management. This is the entry point for all users before they can apply for credit.

## Implementation Status

| Story | Status | Description |
|-------|--------|-------------|
| #21 | ✅ Done | Customer Registration |
| #22 | ✅ Done | Email Verification |
| #23 | ✅ Done | Profile Completion |
| #24 | ✅ Done | Profile Management |

## Package Structure

```
me.karun.bank.credit.customer/
├── api/                    # Public API (interfaces, DTOs)
│   ├── CustomerService.java              ✅
│   ├── RegistrationRequest.java          ✅
│   ├── RegistrationResponse.java         ✅
│   ├── CustomerRegisteredEvent.java      ✅
│   ├── InvalidEmailException.java        ✅
│   ├── WeakPasswordException.java        ✅
│   ├── EmailAlreadyExistsException.java  ✅
│   ├── VerifyEmailRequest.java           ✅
│   ├── VerifyEmailResponse.java          ✅
│   ├── ResendVerificationRequest.java    ✅
│   ├── ResendVerificationResponse.java   ✅
│   ├── TokenNotFoundException.java       ✅
│   ├── TokenExpiredException.java        ✅
│   ├── RateLimitExceededException.java   ✅
│   ├── ProfileRequest.java               ✅
│   ├── ProfileResponse.java              ✅
│   ├── ProfileUpdateRequest.java         ✅
│   ├── AddressDto.java                   ✅
│   ├── CustomerNotFoundException.java    ✅
│   └── CustomerNotVerifiedException.java ✅
├── internal/               # Private implementation
│   ├── config/
│   │   └── CustomerConfig.java           ✅
│   ├── domain/
│   │   ├── Customer.java                 ✅
│   │   ├── CustomerStatus.java           ✅
│   │   ├── CustomerProfile.java          ✅
│   │   ├── Address.java                  ✅
│   │   ├── ProfileAudit.java             ✅
│   │   └── VerificationToken.java        ✅
│   ├── repository/
│   │   ├── CustomerRepository.java       ✅
│   │   ├── CustomerProfileRepository.java ✅
│   │   ├── ProfileAuditRepository.java   ✅
│   │   └── VerificationTokenRepository.java ✅
│   ├── service/
│   │   ├── CustomerServiceImpl.java      ✅
│   │   └── VerificationTokenService.java ✅
│   └── validation/
│       ├── AdultAge.java                 ✅
│       ├── AdultAgeValidator.java        ✅
│       ├── ValidSsn.java                 ✅
│       └── SsnValidator.java             ✅
└── web/                    # REST controllers
    ├── CustomerController.java           ✅
    └── CustomerExceptionHandler.java     ✅
```

## Database Schema

**Schema name**: `customer`

```sql
-- customers table
CREATE TABLE customer.customers (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    verified_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- customer_profiles table
CREATE TABLE customer.customer_profiles (
    customer_id UUID PRIMARY KEY REFERENCES customer.customers(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    ssn_encrypted VARCHAR(255) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- verification_tokens table
CREATE TABLE customer.verification_tokens (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customer.customers(id),
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

-- profile_audit table
CREATE TABLE customer.profile_audit (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customer.customers(id),
    field_name VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_at TIMESTAMP NOT NULL,
    changed_by UUID NOT NULL
);
```

## Public API

### REST Endpoints

| Method | Path | Description | Status |
|--------|------|-------------|--------|
| POST | `/api/v1/customers` | Register new customer | ✅ |
| POST | `/api/v1/customers/verify-email` | Verify email | ✅ |
| POST | `/api/v1/customers/resend-verification` | Resend verification email | ✅ |
| PUT | `/api/v1/customers/{customerId}/profile` | Complete/update profile | ✅ (temp auth) |
| GET | `/api/v1/customers/{customerId}/profile` | Get profile | ✅ (temp auth) |
| PATCH | `/api/v1/customers/{customerId}/profile` | Partial profile update | ✅ (temp auth) |

### CustomerService Interface

```java
public interface CustomerService {
    RegistrationResponse register(RegistrationRequest request);           // ✅ Implemented
    VerifyEmailResponse verifyEmail(VerifyEmailRequest request);          // ✅ Implemented
    ResendVerificationResponse resendVerification(ResendVerificationRequest request); // ✅ Implemented
    ProfileResponse completeProfile(String customerId, ProfileRequest request); // ✅ Implemented
    ProfileResponse getProfile(String customerId);                        // ✅ Implemented
    ProfileResponse updateProfile(String customerId, ProfileUpdateRequest request); // ✅ Implemented
    boolean isProfileComplete(String customerId);                         // Planned
}
```

### Events Published

| Event | When | Payload |
|-------|------|---------|
| `CustomerRegisteredEvent` | After registration | customerId, email |
| `CustomerVerifiedEvent` | After email verification | customerId |
| `ProfileCompletedEvent` | After profile completion | customerId |

### Events Consumed

None - customer module is the entry point.

## Dependencies

### Depends On
- `shared:kernel` - Base classes, common utilities
- `shared:infrastructure` - Security, encryption utilities

### Depended On By
- `application` - Queries customer profile for credit applications
- `account` - Links accounts to customers
- `support` - Customer lookup for support cases

## Integration Points

### Outbound
- **Email Service** (future): Sends verification emails
- **Audit Log**: All operations logged

### Inbound
- **API Gateway**: All REST endpoints via gateway

## Security Considerations

- SSN encrypted using AES-256 before storage
- Password hashed with BCrypt (strength 12)
- Email verification tokens are hashed (one-way)
- Rate limiting on verification resend
- All PII access logged to audit

## Related

- **Domain**: `docs/context/domain/customer.md`
- **Epic**: #1 E01: Customer Onboarding
- **Stories**: #21, #22, #23, #24
