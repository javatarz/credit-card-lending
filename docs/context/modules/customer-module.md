# Customer Module

## Responsibility

Owns customer identity, registration, verification, and profile management. This is the entry point for all users before they can apply for credit.

## Package Structure

```
me.karun.bank.credit.customer/
├── api/                    # Public API (interfaces, DTOs)
│   ├── CustomerService.java
│   ├── dto/
│   │   ├── RegistrationRequest.java
│   │   ├── RegistrationResponse.java
│   │   ├── ProfileRequest.java
│   │   └── ProfileResponse.java
│   └── events/
│       ├── CustomerRegisteredEvent.java
│       ├── CustomerVerifiedEvent.java
│       └── ProfileCompletedEvent.java
├── internal/               # Private implementation
│   ├── domain/
│   │   ├── Customer.java
│   │   ├── CustomerProfile.java
│   │   ├── CustomerStatus.java
│   │   └── VerificationToken.java
│   ├── repository/
│   │   ├── CustomerRepository.java
│   │   └── VerificationTokenRepository.java
│   ├── service/
│   │   └── CustomerServiceImpl.java
│   └── validation/
│       └── PasswordValidator.java
└── web/                    # REST controllers
    └── CustomerController.java
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
```

## Public API

### CustomerService Interface

```java
public interface CustomerService {
    RegistrationResponse register(RegistrationRequest request);
    void verifyEmail(String token);
    void resendVerification(String email);
    ProfileResponse getProfile(String customerId);
    ProfileResponse updateProfile(String customerId, ProfileRequest request);
    boolean isProfileComplete(String customerId);
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
