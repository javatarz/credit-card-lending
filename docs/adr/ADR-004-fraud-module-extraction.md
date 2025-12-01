# ADR-004: Fraud Module as Extractable Service

## Status

Accepted

## Context

Fraud ML models have different deployment requirements than the main application:
- Model retraining cycles are independent of feature development
- Model updates shouldn't require full platform deployment
- Fraud scoring needs to scale independently under transaction load
- ML infrastructure (model serving) has different operational patterns

## Decision

Design the fraud module with clear API boundaries from day one, deployed as a separate ECS service even in MVP.

### Architecture

```
┌─────────────────────────────────────────────────────────────┐
│  ECS Cluster                                                │
│  ┌───────────────────────────────┐  ┌────────────────────┐  │
│  │  Platform Service             │  │  Fraud ML Service  │  │
│  │  (Modular Monolith)           │  │  (Separate Deploy) │  │
│  │                               │  │                    │  │
│  │  ┌─────────────────────────┐  │  │  ┌──────────────┐  │  │
│  │  │  Fraud Client Module    │──┼──┼─▶│  Fraud API   │  │  │
│  │  │  (HTTP Client)          │  │  │  │              │  │  │
│  │  └─────────────────────────┘  │  │  └──────────────┘  │  │
│  │                               │  │         │         │  │
│  └───────────────────────────────┘  │  ┌──────▼───────┐  │  │
│                                     │  │  ML Model    │  │  │
│                                     │  │  Serving     │  │  │
│                                     │  └──────────────┘  │  │
│                                     └────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Fraud Service API Contract

```java
// Contract interface (shared)
public interface FraudScoringService {

    ApplicationFraudScore scoreApplication(ApplicationFraudRequest request);

    TransactionFraudScore scoreTransaction(TransactionFraudRequest request);
}

// Request/Response DTOs
public record ApplicationFraudRequest(
    String applicationId,
    String customerId,
    BigDecimal annualIncome,
    String ssn,  // Hashed
    LocalDate dateOfBirth,
    Address address,
    Instant applicationTimestamp
) {}

public record ApplicationFraudScore(
    String applicationId,
    double score,           // 0.0 - 1.0
    RiskLevel riskLevel,    // LOW, MEDIUM, HIGH
    List<String> signals,   // Contributing factors
    Instant scoredAt
) {}

public record TransactionFraudRequest(
    String transactionId,
    String accountId,
    String cardToken,
    BigDecimal amount,
    String merchantId,
    String merchantCategory,
    String merchantCountry,
    Instant transactionTime
) {}

public record TransactionFraudScore(
    String transactionId,
    double score,
    RiskLevel riskLevel,
    boolean shouldDecline,
    List<String> signals,
    Instant scoredAt
) {}
```

### Implementation Strategy

**MVP Phase**:
- Fraud service deployed as separate ECS task
- Simple rules-based scoring (no ML yet)
- HTTP communication via internal ALB

**Post-MVP**:
- Integrate ML model serving (SageMaker or custom)
- Add model versioning and A/B testing
- Implement feature store for real-time features

### Client Module (in Platform)

```java
@FeignClient(name = "fraud-service", url = "${fraud.service.url}")
public interface FraudServiceClient extends FraudScoringService {

    @PostMapping("/api/v1/fraud/application/score")
    ApplicationFraudScore scoreApplication(@RequestBody ApplicationFraudRequest request);

    @PostMapping("/api/v1/fraud/transaction/score")
    TransactionFraudScore scoreTransaction(@RequestBody TransactionFraudRequest request);
}
```

### Performance Requirements

| Metric | Requirement |
|--------|-------------|
| Application scoring latency | < 500ms p99 |
| Transaction scoring latency | < 50ms p99 |
| Availability | 99.9% |
| Throughput | 1000 RPS (transaction scoring) |

### Circuit Breaker

```java
@CircuitBreaker(name = "fraudService", fallbackMethod = "fallbackScore")
public TransactionFraudScore scoreTransaction(TransactionFraudRequest request) {
    return fraudServiceClient.scoreTransaction(request);
}

// Fallback: allow transaction but flag for review
private TransactionFraudScore fallbackScore(TransactionFraudRequest request, Exception e) {
    return new TransactionFraudScore(
        request.transactionId(),
        0.5,
        RiskLevel.MEDIUM,
        false,  // Don't decline on service failure
        List.of("FRAUD_SERVICE_UNAVAILABLE"),
        Instant.now()
    );
}
```

## Consequences

### Positive

- Fraud models deployable independently
- Clear contract between platform and fraud scoring
- Can scale fraud service independently under load
- Allows specialized ML infrastructure
- Model updates don't require platform changes

### Negative

- Additional service to deploy and monitor
- Network latency for fraud calls
- More complex local development setup
- Need to handle service unavailability

### Mitigation

- Circuit breaker pattern for resilience
- Local mock for development
- Comprehensive contract tests
- Shared API contract library
