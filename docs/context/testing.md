# Testing Strategy

## Philosophy

We practice **Test-Driven Development (TDD)** as our primary development approach. Tests are not an afterthought - they drive design and provide confidence for change.

> "Clean code that works" - Ron Jeffries (the goal of TDD)

## TDD Principles

### Kent Beck's Two Rules

1. **Write new code only if an automated test has failed**
2. **Eliminate duplication** - between test and implementation, and within code

### The Three Laws of TDD (Uncle Bob)

1. You are not allowed to write any production code unless it is to make a failing unit test pass
2. You are not allowed to write any more of a unit test than is sufficient to fail (compilation failures are failures)
3. You are not allowed to write any more production code than is sufficient to pass the one failing unit test

### Red-Green-Refactor

| Phase | Focus | Goal |
|-------|-------|------|
| **RED** | What it should do | Write a failing test |
| **GREEN** | Make it work | Minimum code to pass |
| **REFACTOR** | Make it right | Clean up while staying green |

### Three Techniques for Getting to Green

| Technique | When to Use | How it Works |
|-----------|-------------|--------------|
| **Fake It** | When unsure of implementation | Return a constant, gradually replace with variables |
| **Obvious Implementation** | When you know exactly what to type | Write the real implementation directly |
| **Triangulation** | When the right design isn't clear | Add multiple assertions to clarify what implementation is needed |

**Moving at different speeds:**
- Obvious Implementation is "second gear" - use when confident
- Downshift to Fake It when your brain starts writing checks your fingers can't cash
- Use Triangulation when stuck - add more test cases to reveal the pattern

**Note:** Duplication between test and implementation signals incomplete implementation (something is hard-coded). This is OK temporarily - fix it in refactor or the next cycle.

## Test Strategy

### Current Approach: Test Pyramid

```
        /\
       /  \  E2E (few)
      /----\
     /      \  Integration (some)
    /--------\
   /          \  Unit (many)
  --------------
```

We start with a **Test Pyramid** approach:
- **Many unit tests**: Fast, isolated, test business logic
- **Some integration tests**: Test module boundaries, database interactions
- **Few E2E tests**: Critical user journeys only

**This is not dogma.** We review periodically and adjust based on:
- Where bugs are escaping
- What gives us confidence
- What slows us down

The shape may evolve to diamond or trophy if that serves us better.

### When to Use Each Test Type

| Test Type | Use For | Characteristics |
|-----------|---------|-----------------|
| **Unit** | Business logic, domain rules, calculations, validations | Fast (<10ms), no I/O, mocked dependencies |
| **Integration** | Module boundaries, repository queries, API contracts | Real DB (Testcontainers), slower but realistic |
| **Architecture** | Module dependency rules, package structure | ArchUnit, run with unit tests |
| **E2E** | Critical user journeys, smoke tests | Full stack, expensive, few |

### Balanced Mix for Spring Boot Modular Monolith

- **Unit tests** for domain logic within modules (`internal/domain/`, `internal/service/`)
- **Integration tests** for:
  - Repository queries (with Testcontainers PostgreSQL)
  - REST controllers (with MockMvc or WebTestClient)
  - Cross-module interactions via public APIs
- **ArchUnit tests** for enforcing module boundaries

## What Makes a Good Test

### Properties (MC-FIRE)

| Property | Meaning |
|----------|---------|
| **M**aintainable | Easy to update when requirements change |
| **C**omplete | Tests the full behavior, not just happy path |
| **F**ast | Runs quickly (unit <10ms, integration <1s) |
| **I**solated | No dependency on other tests or external state |
| **R**epeatable | Same result every time, anywhere |
| **E**xpressive | Clearly communicates intent |

### A Good Test Reads Like Documentation

```java
@Test
void shouldRejectRegistration_whenEmailAlreadyExists() {
    var existingCustomer = customers.withEmail("taken@example.com");

    var result = customerService.register(aRegistrationRequest()
        .withEmail("taken@example.com")
        .build());

    assertThat(result).isFailure()
        .hasErrorCode("EMAIL_ALREADY_EXISTS");
}
```

### Test Structure: Arrange-Act-Assert (AAA)

Every test has three distinct sections, separated by blank lines:

| Section | Purpose | Rule |
|---------|---------|------|
| **Arrange** | Set up preconditions and inputs | Single block, no blank lines within |
| **Act** | Execute the behavior under test | **Single line only** |
| **Assert** | Verify the expected outcome | **One logical assertion** |

**Do not use comments** (`// Given`, `// When`, `// Then`) - the structure should be clear from the code itself.

#### The Act Section

The Act section must be a **single line**. This forces clarity about what exactly is being tested. Use a consistent variable name:

```java
var result = service.register(request);
```

If you struggle to write a single line, extract setup logic into Arrange or use a helper method.

#### The Assert Section

Each test should verify **one logical concept**. Prefer a single assertion, but related assertions on the same result are acceptable:

```java
assertThat(result.email()).isEqualTo("user@example.com");
```

Multiple assertions on the same result object are acceptable when they verify one logical concept:

```java
assertThat(result.customerId()).isNotNull();
assertThat(result.status()).isEqualTo("PENDING_VERIFICATION");
```

If you need unrelated assertions, write separate tests.

### What to Test

| Do Test | Don't Test |
|---------|------------|
| Business rules and domain logic | Framework code (Spring, JPA) |
| Edge cases and error conditions | Getters/setters without logic |
| State transitions | Private methods directly |
| Integration points | Third-party library internals |
| Validation rules | Trivial code |

### Test One Thing

Each test should verify one logical concept. If a test fails, it should be immediately obvious what broke.

**Bad**: `testCustomerRegistration()` - tests 5 things, unclear what failed
**Good**: `shouldSendVerificationEmail_whenRegistrationSucceeds()`

## DRY Tests

Tests should be DRY (Don't Repeat Yourself). Extract common setup, use parameterized tests for variations.

### Parameterized Tests with @MethodSource

Prefer `@MethodSource` for data-driven tests. It's the most flexible approach for multiple parameters and complex data types.

```java
@ParameterizedTest
@MethodSource("invalidEmailCases")
void shouldRejectRegistration_whenEmailIsInvalid(String email, String expectedError) {
    var request = aRegistrationRequest().withEmail(email).build();

    var result = customerService.register(request);

    assertThat(result).isFailure().hasErrorCode(expectedError);
}

private static Stream<Arguments> invalidEmailCases() {
    return Stream.of(
        arguments(null, "EMAIL_REQUIRED"),
        arguments("", "EMAIL_REQUIRED"),
        arguments("not-an-email", "EMAIL_INVALID_FORMAT"),
        arguments("missing@domain", "EMAIL_INVALID_FORMAT"),
        arguments("@nodomain.com", "EMAIL_INVALID_FORMAT")
    );
}
```

### When to Use Parameterized Tests

| Use Parameterized | Use Regular Tests |
|-------------------|-------------------|
| Same logic, different inputs | Different logic paths |
| Boundary value testing | Complex setup per case |
| Validation rules with multiple cases | When test names would be unclear |
| Error code verification | When debugging one case is likely |

### Extracting Common Setup

```java
// Bad - repeated setup in each test
@Test
void test1() {
    var customer = new Customer();
    customer.setEmail("test@example.com");
    customer.setStatus(VERIFIED);
    // ... test logic
}

@Test
void test2() {
    var customer = new Customer();
    customer.setEmail("test@example.com");
    customer.setStatus(VERIFIED);
    // ... test logic
}

// Good - extracted to Object Mother or @BeforeEach
@BeforeEach
void setUp() {
    customer = TestCustomers.verified();
}
```

## Test Tooling

| Tool | Purpose |
|------|---------|
| **JUnit 5** | Test framework |
| **AssertJ** | Fluent assertions |
| **Mockito** | Mocking framework |
| **Testcontainers** | Real PostgreSQL for integration tests |
| **ArchUnit** | Architecture rule enforcement |

### Assertion Style

Prefer AssertJ fluent assertions:

```java
// Good - AssertJ
assertThat(customer.getStatus()).isEqualTo(VERIFIED);
assertThat(customers).hasSize(3).extracting("email").contains("a@b.com");

// Avoid - JUnit assertions
assertEquals(VERIFIED, customer.getStatus());
```

## Test Data

### Object Mother Pattern

Use Object Mothers for common test fixtures. Object Mothers internally use builders.

```java
// Object Mother - provides common test scenarios
public class TestCustomers {
    public static Customer verifiedCustomer() {
        return aCustomer()
            .withStatus(VERIFIED)
            .withEmail("verified@example.com")
            .build();
    }

    public static Customer unverifiedCustomer() {
        return aCustomer()
            .withStatus(PENDING_VERIFICATION)
            .build();
    }
}

// Builder - for custom scenarios in specific tests
public class CustomerBuilder {
    // Use Lombok @Builder or manual builder
    public static CustomerBuilder aCustomer() {
        return new CustomerBuilder()
            .withId(UUID.randomUUID())
            .withEmail("default@example.com")
            .withStatus(PENDING_VERIFICATION);
    }
}
```

**Usage:**
```java
// Use Object Mother for common cases
var customer = TestCustomers.verifiedCustomer();

// Use builder for specific test needs
var customer = aCustomer()
    .withEmail("specific@example.com")
    .withStatus(SUSPENDED)
    .build();
```

## Anti-Patterns to Avoid

| Anti-Pattern | Problem | Instead |
|--------------|---------|---------|
| **Testing implementation** | Brittle, breaks on refactor | Test behavior and outcomes |
| **Excessive mocking** | Tests don't reflect reality | Use real objects where practical |
| **Shared mutable state** | Tests affect each other | Fresh fixtures per test |
| **Testing private methods** | Couples to implementation | Test via public interface |
| **Ignoring failing tests** | Erodes trust in suite | Fix or delete immediately |
| **Slow tests** | Developers skip them | Keep unit tests fast |

## References

- **Test Driven Development by Example** - Kent Beck
- [Test-Driven Development](https://martinfowler.com/bliki/TestDrivenDevelopment.html) - Martin Fowler
- [The Cycles of TDD](https://blog.cleancoder.com/uncle-bob/2014/12/17/TheCyclesOfTDD.html) - Uncle Bob
- [Write tests. Not too many. Mostly integration.](https://kentcdodds.com/blog/write-tests) - Kent C. Dodds (Test Trophy perspective)
