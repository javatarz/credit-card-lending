# ADR-005: Processor Integration (Simulation)

## Status

Accepted

## Context

This is a simulation/learning project. We need to model realistic card network processor behavior without actual Visa/Mastercard integration, which would require:
- Network membership and certification
- Significant compliance requirements
- Production infrastructure agreements

## Decision

Build a mock processor service that simulates card network behavior for testing and development.

### Processor Gateway Interface

```java
public interface ProcessorGateway {

    /**
     * Process an authorization request from a merchant
     */
    AuthorizationResponse authorize(AuthorizationRequest request);

    /**
     * Process a batch of settled transactions
     */
    SettlementResponse settle(SettlementBatch batch);

    /**
     * Reverse a previous authorization
     */
    ReversalResponse reverse(ReversalRequest request);
}
```

### Authorization Flow

```
Merchant → [Acquirer] → Network → [Issuer Processor] → Our System
                                         ↑
                              Simulated by processor-sim module
```

### Request/Response Models

```java
public record AuthorizationRequest(
    String networkTransactionId,
    String cardNumber,          // PAN
    String expiryDate,          // MMYY
    String cvv,                 // For card-not-present
    BigDecimal amount,
    String currencyCode,
    String merchantId,
    String merchantName,
    String merchantCategoryCode,
    String merchantCountry,
    TransactionType type,       // PURCHASE, CASH_ADVANCE, REFUND
    Instant timestamp
) {}

public record AuthorizationResponse(
    String networkTransactionId,
    String authorizationCode,   // 6-digit code if approved
    ResponseCode responseCode,
    String responseMessage,
    BigDecimal approvedAmount,
    Instant processedAt
) {}

public enum ResponseCode {
    APPROVED("00"),
    DECLINED_INSUFFICIENT_FUNDS("51"),
    DECLINED_CARD_EXPIRED("54"),
    DECLINED_INVALID_CARD("14"),
    DECLINED_DO_NOT_HONOR("05"),
    DECLINED_SUSPECTED_FRAUD("59"),
    DECLINED_CARD_RESTRICTED("62"),
    SYSTEM_ERROR("96");

    private final String code;
}
```

### Simulation Capabilities

**MVP**:
- Authorization request/response
- Basic settlement batches
- Configurable decline rates
- Simple rule-based responses

**Post-MVP** (if needed):
- Chargeback simulation
- Network-specific response codes
- Partial authorization
- Multi-currency
- 3DS simulation

### Simulation Modes

```java
public enum SimulationMode {
    ALWAYS_APPROVE,         // All requests approved
    ALWAYS_DECLINE,         // All requests declined
    RULE_BASED,            // Apply configured rules
    RANDOM                 // Random with configurable rates
}

@ConfigurationProperties(prefix = "processor.simulation")
public record SimulationConfig(
    SimulationMode mode,
    double declineRate,     // For RANDOM mode
    List<DeclineRule> rules // For RULE_BASED mode
) {}

public record DeclineRule(
    String field,           // amount, mcc, country
    String operator,        // gt, lt, eq, in
    String value,
    ResponseCode responseCode
) {}
```

### Example Rules

```yaml
processor:
  simulation:
    mode: RULE_BASED
    rules:
      - field: amount
        operator: gt
        value: "10000"
        responseCode: DECLINED_DO_NOT_HONOR
      - field: mcc
        operator: in
        value: "7995,7994"  # Gambling
        responseCode: DECLINED_CARD_RESTRICTED
      - field: country
        operator: not_eq
        value: "US"
        responseCode: DECLINED_DO_NOT_HONOR
```

### Settlement Simulation

```java
public record SettlementBatch(
    String batchId,
    LocalDate settlementDate,
    List<SettlementTransaction> transactions
) {}

public record SettlementTransaction(
    String networkTransactionId,
    String authorizationCode,
    BigDecimal settledAmount,
    BigDecimal interchangeFee,
    String merchantId
) {}

public record SettlementResponse(
    String batchId,
    int processedCount,
    int failedCount,
    List<SettlementError> errors
) {}
```

### Test UI

Provide a simple UI for manual testing:
- Submit authorization requests
- View transaction history
- Configure simulation rules
- Trigger settlement batches

```
POST /api/internal/processor/test/authorize
POST /api/internal/processor/test/settle
GET  /api/internal/processor/test/config
PUT  /api/internal/processor/test/config
```

## Consequences

### Positive

- Realistic testing without network dependencies
- Can add complexity incrementally
- Interface allows swap to real processor if needed
- Full control over test scenarios
- No compliance overhead for development

### Negative

- Not a true representation of network behavior
- Edge cases may not be simulated
- No real network certification testing

### Future Migration Path

If real processor integration is needed:
1. Implement `ProcessorGateway` for real network
2. Add network-specific message formatting (ISO 8583)
3. Implement HSM integration for cryptographic operations
4. Add network certification test suite
