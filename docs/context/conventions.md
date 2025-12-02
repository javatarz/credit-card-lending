# Code Conventions

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

### Test Class Names
| Type | Pattern | Example |
|------|---------|---------|
| Unit | `<Class>Test` | `CustomerServiceTest` |
| Integration | `<Class>IntegrationTest` | `CustomerControllerIntegrationTest` |
| Architecture | `<Module>ArchitectureTest` | `CustomerArchitectureTest` |

### Test Method Names
```java
@Test
void shouldCreateCustomer_whenValidRequest() { }

@Test
void shouldThrowException_whenEmailAlreadyExists() { }

@Test
void shouldReturnNotFound_whenCustomerDoesNotExist() { }
```

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
