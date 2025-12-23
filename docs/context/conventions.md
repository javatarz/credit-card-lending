# Code Conventions

## Why Read This?

Code style, naming patterns, and git workflow. Read before writing or reviewing code.

**Prerequisites:** [overview.md](overview.md)
**Related:** [testing.md](testing.md) for TDD and test strategy

---

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

### Self-Documenting Code (No Comments)

Code should be self-documenting. **Do not add comments** to explain what code does.

| Instead of... | Do this... |
|---------------|------------|
| Comments explaining logic | Use descriptive method/variable names |
| Comments marking sections | Extract to well-named methods |
| `// Given / When / Then` in tests | Use blank lines to separate sections |
| Javadoc on internal code | Write clear method signatures |

**When comments ARE acceptable:**
- Public API documentation (Javadoc on `api/` package interfaces)
- Regulatory/compliance requirements that mandate documentation
- `TODO` for tracked technical debt (with issue reference)
- Explaining *why* not *what* (rare business rule clarification)

**Tests as documentation:** Test method names should clearly describe behavior. The test body should be simple enough to understand without comments.

```java
@Test
void should_normalize_email_to_lowercase_when_registering() {
    var request = new RegistrationRequest("User@EXAMPLE.COM", "SecurePass123!");

    var response = service.register(request);

    assertThat(response.email()).isEqualTo("user@example.com");
}
```

### Clean Code Principles (Uncle Bob)

Based on Robert C. Martin's "Clean Code: A Handbook of Agile Software Craftsmanship".

#### Functions

| Rule | Guidance |
|------|----------|
| **Small** | Functions should be small. Then smaller. Ideally 5-10 lines. |
| **Do one thing** | A function should do one thing, do it well, and do it only. |
| **One level of abstraction** | Statements in a function should be at the same abstraction level. |
| **Descriptive names** | Long descriptive names are better than short cryptic ones. |
| **Few arguments** | Ideal is zero (niladic), then one (monadic), then two (dyadic). Avoid three+ (triadic). |
| **No side effects** | Don't do hidden things. If a function is called `checkPassword`, don't also initialize a session. |
| **No flag arguments** | Don't pass booleans. Split into two functions instead. |

#### Naming

| Rule | Guidance |
|------|----------|
| **Intention-revealing** | Names should tell you why it exists, what it does, and how it's used. |
| **Pronounceable** | Use names you can say out loud: `generationTimestamp` not `genymdhms`. |
| **Searchable** | Longer names for larger scope. Single-letter names only for tiny local scope. |
| **No encodings** | Don't prefix with type info (`strName`) or member prefixes (`m_`). |
| **Nouns for classes** | `Customer`, `Account`, `AddressParser` - not `Manager`, `Processor`, `Data`. |
| **Verbs for methods** | `postPayment()`, `deletePage()`, `save()`. |

#### Boy Scout Rule

> "Leave the campground cleaner than you found it."

When you touch code, improve it slightly. Rename a confusing variable. Extract a method. Remove dead code. Small continuous improvements compound over time.

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

Use camelCase with underscore separating the "should" clause from the "when" clause:

| Scenario | Pattern | Example |
|----------|---------|---------|
| With condition | `should<Expected>_when<Condition>` | `shouldRejectRegistration_whenPasswordIsEmpty()` |
| Without condition | `should<Expected>` | `shouldNormalizeEmailToLowercase()` |

**Rules:**
- Use camelCase throughout (Java-idiomatic)
- Underscore `_` only separates "should" from "when" clause
- Tests with same "should" clause but different conditions share the prefix
- Tests with single scenario (no variations) omit the "when" clause

```java
@Test
void shouldCreateCustomer_whenValidRequest() { }

@Test
void shouldThrowException_whenEmailAlreadyExists() { }

@Test
void shouldReturnNotFound_whenCustomerDoesNotExist() { }

@Test
void shouldNormalizeEmailToLowercase() { }

@Test
void shouldHashPasswordWithBCrypt() { }
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

## Git Workflow

### Trunk-Based Development

We use **trunk-based development** - all work happens on `main`:

| Practice | Description |
|----------|-------------|
| **No feature branches** | Commit directly to `main` |
| **No pull requests** | Push directly after local verification |
| **Small commits** | Each Red-Green-Refactor cycle = one commit |
| **Always green** | Never push failing tests |
| **Continuous integration** | Build must pass after each push |

### Why Trunk-Based?

- **Faster feedback** - No waiting for PR reviews
- **Reduced merge conflicts** - Small, frequent integrations
- **Simpler workflow** - Less git ceremony
- **Encourages small changes** - Easier to review history

### Commit Message Rules

| Rule | Description |
|------|-------------|
| **Imperative mood** | "Add feature" not "Added feature". Test: "If applied, this commit will _[subject]_" |
| **Subject ≤ 50 chars** | Aim for 50, hard limit 72. Forces conciseness. (excludes issue reference) |
| **Capitalize subject** | "Add validation" not "add validation" |
| **No trailing period** | Wastes space, unnecessary |
| **Reference the issue** | Append `#N` for traceability. Omit only for trivial chores. |
| **One logical change** | Each Red-Green-Refactor cycle = one commit. Enables clean reverts. |
| **Body explains why** | Code shows how. Body explains motivation, trade-offs, context. |
| **No manual line wrap** | Let tools handle body text display |

### Commit Message Format

```
<subject> #N

<optional body - why, not how>

<optional: Closes #N>
```

### Closing Issues

Use `Closes #N` in commit footer to auto-close issues when pushed. This ties closure to the code change atomically.

| Footer Keyword | Effect |
|----------------|--------|
| `Closes #N` | Closes issue #N when commit is pushed to default branch |
| `Fixes #N` | Same as Closes (use for bug fixes) |

**When to close:** Include `Closes #N` only in the **final commit** of a story/task. Intermediate commits reference the issue (`#N` in subject) but don't close it.

### Type Prefixes

Start subjects with a verb indicating the change type:

| Prefix | Use When |
|--------|----------|
| `Add` | New feature, file, or capability |
| `Fix` | Bug fix |
| `Update` | Enhance existing feature |
| `Remove` | Delete code, feature, or file |
| `Refactor` | Code change with no behavior change |
| `Rename` | Rename file, class, or variable |
| `Move` | Relocate code without changing it |
| `Docs` | Documentation only |
| `Test` | Test only (no production code) |
| `Config` | Configuration changes |

### Commit Message Examples

```bash
# Good: focused, traceable
git commit -m "Add email validation to registration #22"
git commit -m "Add VerificationToken entity with expiry #22"
git commit -m "Fix null pointer when customer email missing #25"

# Good: with body for complex changes
git commit -m "Add rate limiting to registration endpoint #28" -m "
Prevents abuse by limiting to 5 attempts per IP per hour.
Uses bucket4j with Redis backend.

Closes #28"

# Bad: vague, bundled, or missing context
git commit -m "Fix bug"                    # Too vague, no issue
git commit -m "Added email validation"     # Past tense
git commit -m "Implement registration with validation, events, and tests"  # Too many changes
```

### What NOT to Include in Commits

- **No file lists** - The diff shows what changed
- **No "how" explanations** - Code is self-documenting; explain "why" instead

### Before Pushing

Always verify locally:
```bash
./gradlew build   # Must pass
git push origin main
```

### When to Use Branches

Exceptions where branches are acceptable:
- **Experimental spikes** - Exploring options before committing to approach
- **Large refactors** - When you need to share WIP with others
- **External contributions** - Contributors without push access
