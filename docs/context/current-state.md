# Current State

## Why Read This?

Quick reference for what's built vs planned. Check here before starting work to understand the current implementation status.

**Prerequisites:** [overview.md](overview.md)
**Related:** [modules/](modules/) for detailed module status

---

> **Freshness:** Run `git log -1 --format="%h %s (%ar)" docs/context/current-state.md` to see last update.

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

### Documentation & Onboarding (Stories #31, #32, #33, #27)
- [x] README.md with project overview and quick start
- [x] Documentation map (Wiki, context docs, ADRs, Issues)
- [x] iE workflow introduction (`/pickup`, `/start-dev`)
- [x] MIT License
- [x] intelligent Engineering (iE) Wiki Guide
  - iE principles and implementation phases (Sahaj attribution)
  - What we do vs don't yet do
  - Workflow and documentation landscape diagrams
  - Getting started guide
- [x] Wiki Home.md updated with iE focus
- [x] Wiki enhancements:
  - Context-Documentation.md links to key context docs
  - Architecture.md Mermaid diagrams (system architecture, transaction flow)
  - Architecture.md tech stack fixed (Java 25, Spring Boot 4.x)
  - MVP.md Mermaid user journey diagram
- [x] Developer onboarding (Story #27):
  - `scripts/setup.sh` - One-command development setup
  - `CONTRIBUTING.md` - Comprehensive contribution guidelines
  - Gradle docker-compose plugin - Auto-starts PostgreSQL on `bootRun`
  - Trunk-based development workflow documented
- [x] Context documentation efficiency (Story #43):
  - `docs/context/README.md` - Navigation map with "I want to..." goals
  - Domain and module templates (`docs/context/domain/README.md`, `docs/context/modules/README.md`)
  - "Why read this?" headers on all context files
  - `CLAUDE.md` reduced from 166 → 75 lines
  - Story template extracted to `docs/templates/story.md`
  - Tech stack consolidated to `overview.md` as single source
  - Pre-commit hook for markdown link validation
  - GitHub Action for CI link validation
  - Commit message conventions documented (Story #44)

### Testing & Developer Workflow (Story #29)
- [x] Testing strategy documented (`docs/context/testing.md`)
- [x] TDD principles (Kent Beck, Uncle Bob)
- [x] Test conventions (naming, structure, tooling)
- [x] Design principles (SOLID, DRY, YAGNI, SRP)
- [x] `/start-dev` command for TDD workflow
- [x] Parameterized test patterns (@MethodSource)
- [x] Object Mother + Builder pattern for test data

### Feature Modules

#### Customer Module (Stories #21, #22)
- [x] Customer registration (`POST /api/v1/customers`)
- [x] Email validation (format, uniqueness)
- [x] Password validation (8+ chars, uppercase, lowercase, number, special char)
- [x] Password hashing with BCrypt (configurable strength)
- [x] CustomerRegisteredEvent published on success
- [x] RFC 7807 Problem Details for errors
- [x] Swagger/OpenAPI documentation
- [x] Email verification (`POST /api/v1/customers/verify-email`)
- [x] Resend verification (`POST /api/v1/customers/resend-verification`)
- [x] Verification tokens (UUID, 24-hour expiry, hashed storage)
- [x] Rate limiting on resend (max 3 per hour)
- [x] Token generation on CustomerRegisteredEvent

### CI/CD
- [x] GitHub Action for documentation link validation (Story #43)
- [ ] No automated testing pipeline

### Database
- [x] Liquibase migrations for customer module
- [x] `customer` schema with `customers` table
- [x] `customer.verification_tokens` table for email verification

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

**Documentation (P0 - complete):**
1. ~~#31 - S21.2: Create Repository README.md~~ ✓ Complete
2. ~~#32 - S21.3: Create intelligent Engineering Wiki Guide~~ ✓ Complete
3. ~~#33 - S21.4: Wiki Enhancements~~ ✓ Complete
4. ~~#27 - Developer Onboarding & CONTRIBUTING.md~~ ✓ Complete

**Customer Module (P0 - in progress):**
1. ~~#21 - S01.1: Customer Registration~~ ✓ Complete
2. ~~#22 - S01.2: Email Verification~~ ✓ Complete
3. #23 - S01.3: Profile Completion
4. #24 - S01.4: Profile Management

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
