# Current State

Last updated: 2024-12 (Story #28)

## What's Built

### Infrastructure (Story #25)
- [x] Gradle multi-module project structure
- [x] `shared/kernel`, `shared/infrastructure`, `shared/events` modules
- [x] `platform/api-gateway` module (Spring Boot entry point)
- [x] Auto-discovery for feature modules in `modules/`
- [x] `./gradlew createModule -PmoduleName=<name>` task for scaffolding new modules
- [x] Docker Compose with PostgreSQL 16 for local development
- [x] Health check endpoint at `/actuator/health`
- [x] Swagger UI at `/swagger-ui.html`
- [x] ADR documentation structure

### Feature Modules
- [ ] No feature modules implemented yet (use `./gradlew createModule` to create)

### CI/CD
- [ ] GitHub Actions not configured
- [ ] No automated testing pipeline

### Database
- [ ] No Liquibase migrations (structure in place per module)
- [ ] No database schemas created

## MVP Scope

MVP covers the core credit card lifecycle. See epics labeled `mvp`.

### MVP Epics (in order)
1. **E01: Customer Onboarding** - Registration, verification, profile
2. **E02: Credit Application** - Apply for credit card
3. **E03: Credit Decisioning** - Approve/decline applications
4. **E04: Account Management** - Account and card lifecycle
5. **E05: Card Issuance** - Issue cards to customers
6. **E06: Transaction Authorization** - Approve transactions
7. **E07: Transaction Posting** - Post transactions to accounts
8. **E08: Statement Generation** - Monthly statements
9. **E09: Payment Processing** - Accept payments
10. **E10: Application Fraud Detection** - Fraud scoring for applications
11. **E11: Transaction Fraud Detection** - Real-time fraud scoring
12. **E12: Processor Simulation** - Simulated card processor

### Platform Engineering (Ongoing)
- **E26**: Build, CI/CD, testing, observability

## What's Not MVP

| Feature | Why Deferred |
|---------|--------------|
| Rewards program | Complex, not core |
| Collections | Post-delinquency flow |
| Customer support cases | Can use manual process |
| Card controls (freeze/limits) | Enhancement |
| Account closure | Rare in MVP |
| Notifications module | Console logging sufficient |

## Current Sprint Focus

**Next stories to implement:**
1. #21 - S01.1: Customer Registration
2. #22 - S01.2: Email Verification
3. #23 - S01.3: Profile Completion
4. #24 - S01.4: Profile Management

These establish the customer module and enable credit applications.

## Technical Debt

| Item | Impact | Notes |
|------|--------|-------|
| No CI/CD | Manual testing | Story #27 addresses this |
| No Testcontainers setup | Limited integration tests | Planned |
| No ArchUnit tests | Module boundaries not enforced | Planned |

## Key Decisions Made

See `docs/adr/` for full details:

| ADR | Decision |
|-----|----------|
| ADR-001 | Modular monolith architecture |
| ADR-002 | Schema per module for data isolation |
| ADR-003 | Java 25, Spring Boot 4, PostgreSQL 16 |
| ADR-004 | Fraud module as separate service |
| ADR-006 | Spring events for internal communication |

## Metrics & Monitoring

Not yet implemented. Planned:
- Micrometer metrics
- CloudWatch integration
- OpenTelemetry tracing
