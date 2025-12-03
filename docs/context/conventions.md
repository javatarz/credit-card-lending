# Code Conventions

## Design Principles

### Core Principles

| Principle | Meaning |
|-----------|---------|
| **SOLID** | Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion |
| **DRY** | Don't Repeat Yourself - every piece of knowledge should have a single, unambiguous representation |
| **YAGNI** | You Aren't Gonna Need It - don't add functionality until it's necessary |

### Single Responsibility Principle (Key)

SRP applies at **all levels**:

| Level | Guidance |
|-------|----------|
| **Package** | Each package has one clear purpose. If you can't describe it in one sentence, split it. |
| **Class** | Each class has one reason to change. If it has multiple responsibilities, extract them. |
| **Method** | Each method does one thing. If it needs "and" to describe it, split it. |

Signs you're violating SRP:
- Class/method names with "And" or "Or"
- Methods longer than ~20 lines
- Classes with unrelated methods
- Difficulty writing a concise docstring

## Package Structure

Base package: `me.karun.bank.credit`

### Module Packages
```
me.karun.bank.credit.<module>/
├── api/          # Public interfaces, DTOs, events (can be depended on)
├── internal/     # Private implementation (never depend on)
│   ├── domain/   # Entities, aggregates, value objects
│   ├── repository/
│   └── service/
└── web/          # REST controllers
```

### Visibility Rules
- `api/` package: Public, can be used by other modules
- `internal/` package: Private, enforced by ArchUnit
- `web/` package: REST layer, depends on `api/`

## Naming Conventions

### Classes
| Type | Pattern | Example |
|------|---------|---------|
| Entity | Noun | `Customer`, `Account` |
| Repository | `<Entity>Repository` | `CustomerRepository` |
| Service Interface | `<Domain>Service` | `CustomerService` |
| Service Impl | `<Domain>ServiceImpl` | `CustomerServiceImpl` |
| Controller | `<Domain>Controller` | `CustomerController` |
| Request DTO | `<Action>Request` | `RegistrationRequest` |
| Response DTO | `<Action>Response` | `RegistrationResponse` |
| Event | `<Entity><Action>Event` | `CustomerRegisteredEvent` |

### Methods
| Type | Pattern | Example |
|------|---------|---------|
| Create | `create<Entity>` | `createAccount()` |
| Find single | `get<Entity>` or `find<Entity>` | `getCustomer()` |
| Find multiple | `get<Entity>s` or `findAll<Entity>s` | `getAccounts()` |
| Update | `update<Entity>` | `updateProfile()` |
| Delete | `delete<Entity>` | `deleteCard()` |
| Check | `is<Condition>` or `has<Condition>` | `isVerified()`, `hasProfile()` |

## API Design

### REST Endpoints
```
POST   /api/v1/<resource>           # Create
GET    /api/v1/<resource>/{id}      # Read
PUT    /api/v1/<resource>/{id}      # Update (full)
PATCH  /api/v1/<resource>/{id}      # Update (partial)
DELETE /api/v1/<resource>/{id}      # Delete
GET    /api/v1/<resource>           # List (with pagination)
```

### Current User
```
GET    /api/v1/customers/me/profile  # Own profile
GET    /api/v1/accounts/me           # Own accounts
```

### Response Format
```json
// Success
{
  "customerId": "...",
  "email": "...",
  "status": "..."
}

// Error (RFC 7807 Problem Details)
{
  "type": "https://api.example.com/errors/validation",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Email format is invalid",
  "instance": "/api/v1/customers/register"
}
```

## Error Handling

### Exception Hierarchy
```java
// Base exception
public abstract class DomainException extends RuntimeException {
    public abstract String getErrorCode();
}

// Specific exceptions
public class CustomerNotFoundException extends DomainException { }
public class EmailAlreadyExistsException extends DomainException { }
public class InvalidPasswordException extends DomainException { }
```

### HTTP Status Codes
| Status | When |
|--------|------|
| 200 | Successful GET, PUT, PATCH |
| 201 | Successful POST (created) |
| 204 | Successful DELETE |
| 400 | Validation error |
| 401 | Not authenticated |
| 403 | Not authorized |
| 404 | Resource not found |
| 409 | Conflict (duplicate) |
| 500 | Internal error |

## Database Conventions

### Table Names
- Lowercase, snake_case
- Plural: `customers`, `accounts`, `transactions`

### Column Names
- Lowercase, snake_case
- Foreign keys: `<entity>_id` (e.g., `customer_id`)
- Timestamps: `created_at`, `updated_at`, `deleted_at`
- Booleans: `is_<condition>` (e.g., `is_active`)

### Schema Per Module
Each module owns its schema. Never query another module's schema directly.

## Testing Conventions

See `docs/context/testing.md` for full testing strategy and TDD principles.

### Test Class Names

| Type | Pattern | Example |
|------|---------|---------|
| Unit | `<Class>Test` | `CustomerServiceTest` |
| Integration | `<Class>IntegrationTest` | `CustomerControllerIntegrationTest` |
| Architecture | `<Module>ArchitectureTest` | `CustomerArchitectureTest` |

### Test Method Names

Pattern: `should_<expected>_when_<condition>`

```java
@Test
void should_create_customer_when_valid_request() { }

@Test
void should_throw_exception_when_email_already_exists() { }

@Test
void should_return_not_found_when_customer_does_not_exist() { }
```

### Test Package Structure

```
me.karun.bank.credit.<module>/
├── src/test/java/
│   └── me.karun.bank.credit.<module>/
│       ├── <Class>Test.java              # Unit tests
│       ├── <Class>IntegrationTest.java   # Integration tests
│       └── testdata/                     # Object Mothers and Builders
│           ├── TestCustomers.java        # Object Mother
│           └── CustomerBuilder.java      # Builder
```

### Test Data: Object Mother + Builder

```java
// Object Mother - common scenarios
public class TestCustomers {
    public static Customer verified() {
        return aCustomer().withStatus(VERIFIED).build();
    }
    public static Customer unverified() {
        return aCustomer().withStatus(PENDING_VERIFICATION).build();
    }
}

// Builder - custom scenarios (Lombok @Builder OK here)
@Builder(builderMethodName = "aCustomer")
public class CustomerBuilder {
    @Builder.Default private UUID id = UUID.randomUUID();
    @Builder.Default private String email = "test@example.com";
    @Builder.Default private CustomerStatus status = PENDING_VERIFICATION;
}
```

### Test Tooling

| Tool | Purpose |
|------|---------|
| JUnit 5 | Test framework |
| AssertJ | Fluent assertions (preferred over JUnit assertions) |
| Mockito | Mocking |
| Testcontainers | Real PostgreSQL for integration tests |
| ArchUnit | Module boundary enforcement |

## Event Conventions

### Event Publishing
```java
// Always publish after transaction commits
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onCustomerRegistered(CustomerRegisteredEvent event) { }
```

### Event Naming
- Past tense: `CustomerRegistered`, `AccountCreated`
- Include aggregate ID and minimal payload
- Events are immutable records

## Java Features to Use

| Feature | Use Case |
|---------|----------|
| Records | DTOs, events, value objects |
| Sealed classes | Constrained type hierarchies |
| Pattern matching | Switch expressions |
| Virtual threads | High-throughput I/O |
| Optional | Return types (never parameters) |
