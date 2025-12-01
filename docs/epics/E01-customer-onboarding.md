# E01: Customer Onboarding

## Overview

Enable new customers to register and establish their identity in the system. This is the entry point for all credit card applicants.

## Stories

### S01.1: Customer Registration

**As a** visitor
**I want to** register with my email and password
**So that** I can create an account to apply for a credit card

#### Acceptance Criteria
- [ ] User can submit email and password to register
- [ ] Email must be valid format and unique
- [ ] Password must meet security requirements (min 8 chars, uppercase, lowercase, number, special char)
- [ ] Account created in PENDING_VERIFICATION status
- [ ] Verification email sent automatically
- [ ] Registration timestamp recorded
- [ ] Appropriate error messages for validation failures

#### API Specification

```yaml
POST /api/v1/customers/register
Content-Type: application/json

Request:
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}

Response (201 Created):
{
  "customerId": "cust_abc123",
  "email": "user@example.com",
  "status": "PENDING_VERIFICATION",
  "createdAt": "2024-01-15T10:30:00Z"
}

Error Responses:
- 400 Bad Request: Invalid email format, weak password
- 409 Conflict: Email already registered
```

#### Data Model

```sql
-- Schema: customer
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_status ON customers(status);
```

#### Technical Notes
- Password hashed using BCrypt with strength 12
- Email uniqueness enforced at database level
- Use Spring Validation for input validation

---

### S01.2: Email Verification

**As a** registered user
**I want to** verify my email address
**So that** I can activate my account and proceed with the application

#### Acceptance Criteria
- [ ] Verification email contains unique, time-limited token
- [ ] Token expires after 24 hours
- [ ] User can click link to verify email
- [ ] Account status changes to ACTIVE on verification
- [ ] User can request new verification email
- [ ] Invalid/expired tokens return appropriate error

#### API Specification

```yaml
POST /api/v1/customers/verify-email
Content-Type: application/json

Request:
{
  "token": "eyJhbGciOiJIUzI1NiIs..."
}

Response (200 OK):
{
  "customerId": "cust_abc123",
  "email": "user@example.com",
  "status": "ACTIVE",
  "verifiedAt": "2024-01-15T11:00:00Z"
}

Error Responses:
- 400 Bad Request: Invalid token format
- 410 Gone: Token expired
- 404 Not Found: Token not found
```

```yaml
POST /api/v1/customers/resend-verification
Content-Type: application/json

Request:
{
  "email": "user@example.com"
}

Response (202 Accepted):
{
  "message": "Verification email sent if account exists"
}
```

#### Data Model

```sql
-- Schema: customer
CREATE TABLE email_verifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id),
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_email_verifications_token ON email_verifications(token);
CREATE INDEX idx_email_verifications_customer ON email_verifications(customer_id);
```

#### Technical Notes
- Token generated as secure random UUID
- Email sending is async (don't block registration)
- Rate limit resend requests (max 3 per hour)

---

### S01.3: Profile Completion

**As a** verified user
**I want to** complete my profile with personal information
**So that** I can apply for a credit card

#### Acceptance Criteria
- [ ] User can submit required profile information
- [ ] Required fields: first name, last name, date of birth, SSN, address
- [ ] SSN format validated (XXX-XX-XXXX)
- [ ] Date of birth validated (must be 18+ years old)
- [ ] Address validated (street, city, state, zip)
- [ ] Profile status set to COMPLETE
- [ ] PII data stored securely

#### API Specification

```yaml
PUT /api/v1/customers/profile
Content-Type: application/json
Authorization: Bearer <token>

Request:
{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-05-15",
  "ssn": "123-45-6789",
  "address": {
    "street": "123 Main St",
    "unit": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  },
  "phone": "+1-555-123-4567"
}

Response (200 OK):
{
  "customerId": "cust_abc123",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-05-15",
  "ssnLastFour": "6789",
  "address": {
    "street": "123 Main St",
    "unit": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  },
  "phone": "+1-555-123-4567",
  "profileStatus": "COMPLETE",
  "updatedAt": "2024-01-15T12:00:00Z"
}

Error Responses:
- 400 Bad Request: Validation errors
- 401 Unauthorized: Not authenticated
- 403 Forbidden: Email not verified
```

#### Data Model

```sql
-- Schema: customer
CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL UNIQUE REFERENCES customers(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    ssn_encrypted BYTEA NOT NULL,
    ssn_last_four VARCHAR(4) NOT NULL,
    phone VARCHAR(20),
    profile_status VARCHAR(50) NOT NULL DEFAULT 'INCOMPLETE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE customer_addresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id),
    address_type VARCHAR(50) NOT NULL DEFAULT 'PRIMARY',
    street VARCHAR(255) NOT NULL,
    unit VARCHAR(50),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_customer_profiles_customer ON customer_profiles(customer_id);
CREATE INDEX idx_customer_addresses_customer ON customer_addresses(customer_id);
```

#### Technical Notes
- SSN encrypted using AES-256 before storage
- Only last 4 digits stored in plain text for display
- Age calculated from DOB, must be >= 18
- State must be valid US state code

---

### S01.4: Profile Management

**As a** registered user
**I want to** view and update my profile information
**So that** I can keep my information current

#### Acceptance Criteria
- [ ] User can view their profile information
- [ ] User can update allowed fields (address, phone)
- [ ] SSN and DOB cannot be changed after initial submission
- [ ] Changes are audited
- [ ] Last updated timestamp recorded

#### API Specification

```yaml
GET /api/v1/customers/profile
Authorization: Bearer <token>

Response (200 OK):
{
  "customerId": "cust_abc123",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-05-15",
  "ssnLastFour": "6789",
  "address": {
    "street": "123 Main St",
    "unit": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  },
  "phone": "+1-555-123-4567",
  "status": "ACTIVE",
  "profileStatus": "COMPLETE",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T12:00:00Z"
}
```

```yaml
PATCH /api/v1/customers/profile
Content-Type: application/json
Authorization: Bearer <token>

Request:
{
  "address": {
    "street": "456 Oak Ave",
    "city": "Brooklyn",
    "state": "NY",
    "zipCode": "11201"
  },
  "phone": "+1-555-987-6543"
}

Response (200 OK):
{
  "customerId": "cust_abc123",
  "message": "Profile updated successfully",
  "updatedAt": "2024-01-16T09:00:00Z"
}

Error Responses:
- 400 Bad Request: Invalid fields or trying to update immutable fields
- 401 Unauthorized: Not authenticated
```

#### Technical Notes
- Use PATCH for partial updates
- Immutable fields: email, SSN, DOB, firstName, lastName
- All changes logged to audit schema
- Consider address change verification for fraud prevention

---

## Dependencies

None - this is the entry point epic.

## Test Scenarios

### Registration
1. Successful registration with valid credentials
2. Registration with invalid email format
3. Registration with weak password
4. Registration with existing email
5. Concurrent registration with same email

### Email Verification
1. Successful verification with valid token
2. Verification with expired token
3. Verification with invalid token
4. Resend verification email
5. Rate limiting on resend requests

### Profile Completion
1. Successful profile completion with all fields
2. Profile completion with invalid SSN format
3. Profile completion with age < 18
4. Profile completion with invalid state code
5. Profile completion without email verification

### Profile Management
1. View complete profile
2. Update allowed fields
3. Attempt to update immutable fields
4. View profile without authentication

## Security Considerations

- Passwords hashed with BCrypt (strength 12)
- SSN encrypted with AES-256
- Rate limiting on all endpoints
- JWT tokens for authentication
- HTTPS only
- Input sanitization for XSS prevention
- SQL injection prevention via parameterized queries
