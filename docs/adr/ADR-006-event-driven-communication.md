# ADR-006: Event-Driven Communication (Internal)

## Status

Accepted

## Context

Modules need to communicate without tight coupling. Additionally:
- Audit and compliance require comprehensive event trails
- Some operations are asynchronous (notifications, fraud analysis)
- Future scalability may require external message broker

## Decision

Use Spring's application events for internal module communication. Defer external message broker until scale requires it.

### Event Categories

| Category | Purpose | Examples |
|----------|---------|----------|
| Domain Events | Business state changes | ApplicationSubmitted, CardIssued |
| Integration Events | Cross-module notifications | FraudScoreCalculated |
| Audit Events | Compliance-required recordings | DecisionMade, PaymentProcessed |

### Event Structure

```java
// Base event
public abstract class DomainEvent {
    private final String eventId;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;

    protected DomainEvent(String aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }
}

// Example domain events
public class ApplicationSubmittedEvent extends DomainEvent {
    private final String customerId;
    private final BigDecimal requestedLimit;

    public ApplicationSubmittedEvent(String applicationId, String customerId,
                                      BigDecimal requestedLimit) {
        super(applicationId, "Application");
        this.customerId = customerId;
        this.requestedLimit = requestedLimit;
    }
}

public class ApplicationDecisionMadeEvent extends DomainEvent {
    private final DecisionType decision;
    private final BigDecimal approvedLimit;
    private final List<String> declineReasons;
}

public class AccountCreatedEvent extends DomainEvent {
    private final String customerId;
    private final String applicationId;
    private final BigDecimal creditLimit;
}

public class CardIssuedEvent extends DomainEvent {
    private final String accountId;
    private final String cardToken;  // Not the actual PAN
    private final CardType cardType;
}

public class TransactionAuthorizedEvent extends DomainEvent {
    private final String accountId;
    private final BigDecimal amount;
    private final String merchantName;
    private final AuthorizationResult result;
}

public class PaymentReceivedEvent extends DomainEvent {
    private final String accountId;
    private final BigDecimal amount;
    private final PaymentMethod method;
}
```

### Publishing Events

```java
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Application submitApplication(ApplicationRequest request) {
        Application application = Application.create(request);
        repository.save(application);

        eventPublisher.publishEvent(new ApplicationSubmittedEvent(
            application.getId(),
            request.customerId(),
            request.requestedLimit()
        ));

        return application;
    }
}
```

### Consuming Events

```java
@Component
@RequiredArgsConstructor
public class FraudApplicationListener {

    private final FraudScoringService fraudService;

    @EventListener
    @Async
    public void onApplicationSubmitted(ApplicationSubmittedEvent event) {
        fraudService.scoreApplication(event.getAggregateId());
    }
}

@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditLogRepository auditRepository;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAnyDomainEvent(DomainEvent event) {
        auditRepository.save(AuditLog.from(event));
    }
}
```

### Event Flow Example

```
ApplicationSubmittedEvent
    │
    ├──▶ FraudApplicationListener (async)
    │         └── Scores application
    │
    ├──▶ AuditEventListener
    │         └── Records to audit log
    │
    └──▶ NotificationListener (future)
              └── Sends confirmation email
```

### Transactional Considerations

```java
// For events that must be processed after transaction commits
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleAfterCommit(ApplicationSubmittedEvent event) {
    // External calls, notifications, etc.
}

// For events that must be processed in same transaction
@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
public void handleBeforeCommit(ApplicationSubmittedEvent event) {
    // Validation, derived data updates
}
```

### Future Migration Path

When scale requires external message broker:

```java
// Current: Spring Events
@EventListener
public void handle(ApplicationSubmittedEvent event) { }

// Future: Kafka Consumer
@KafkaListener(topics = "application-events")
public void handle(ApplicationSubmittedEvent event) { }

// Publishing abstraction
public interface EventPublisher {
    void publish(DomainEvent event);
}

// Current implementation
@Component
public class SpringEventPublisher implements EventPublisher {
    private final ApplicationEventPublisher delegate;

    public void publish(DomainEvent event) {
        delegate.publishEvent(event);
    }
}

// Future implementation
@Component
public class KafkaEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, DomainEvent> kafka;

    public void publish(DomainEvent event) {
        kafka.send(event.getAggregateTtype().toLowerCase() + "-events", event);
    }
}
```

## Consequences

### Positive

- Loose coupling between modules
- Natural audit trail via event listeners
- Easy to add new consumers
- Supports both sync and async processing
- No external infrastructure dependency initially

### Negative

- Events only work within single JVM
- No replay capability without event store
- Complex flows harder to trace
- Need careful transaction boundary management

### Mitigation

- Clear event naming conventions
- Correlation IDs for tracing
- Event documentation
- Integration tests for event flows
