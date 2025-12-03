# Project Guidelines

## Documentation Requirements

### Stories & Epics
- GitHub issues are the source of truth for epics and stories
- Every change must have a corresponding GitHub issue

**IMPORTANT: When creating or grooming stories, you MUST use the Story Template below exactly. Never create a story without following this template. All sections are required - use "None" if a section doesn't apply.**

### Documentation Updates
When implementing stories, update relevant documentation:
- **ADRs**: In repo at `docs/adr/` - tied to code changes, reviewed via PRs
- **Design docs**: GitHub Wiki - architecture, features, system design
- **API docs**: Swagger auto-generated from code annotations
- **Code docs**: Module READMEs, inline comments as needed

### Documentation Locations
| Type | Location | Purpose |
|------|----------|---------|
| ADRs | `docs/adr/` (repo) | Technical decisions tied to code |
| Architecture | GitHub Wiki | System design, module overview |
| Features/MVP | GitHub Wiki | Scope and roadmap |
| API docs | Swagger (auto) | Endpoint documentation |
| Epics/Stories | GitHub Issues | Requirements and tracking |

#### Story Template

```markdown
## Description

Purpose of this story. What needs to happen in this card.

## Background / Context

Why we are playing this card.

## Dependency

Highlight any external dependencies which should be ready.
Link dependent upstream and downstream cards.

## Limitations

What will this story not cover.

## Acceptance Criteria

What needs to be done to mark the card as done.

## Test Cases

How do you verify this change works correctly? What are the edge cases that need to be tested?

## Downstream/Upstream Impact

What are other systems that get impacted by this change.

## Tech Notes

Details of the changes that need to be made. Key design changes. Important code changes (high level).

## Context Docs to Update

Which context documents need to be updated as part of this story?
- [ ] `docs/context/overview.md` - System architecture changes
- [ ] `docs/context/glossary.md` - New domain terms
- [ ] `docs/context/domain/<name>.md` - Domain rules or lifecycle changes
- [ ] `docs/context/modules/<name>.md` - Module boundaries or API changes
- [ ] `docs/context/integrations.md` - External system changes
- [ ] `docs/context/conventions.md` - New patterns or standards
- [ ] `docs/context/testing.md` - Test strategy or tooling changes
- [ ] `docs/context/current-state.md` - What's built/planned changes
- [ ] None - no context changes needed
```

### API Documentation
- Use SpringDoc OpenAPI for automatic Swagger documentation
- All REST endpoints must be documented with OpenAPI annotations
- Include request/response examples
- Document error responses
- Swagger UI available at `/swagger-ui.html`
- OpenAPI spec available at `/v3/api-docs`

### Architecture Decision Records (ADRs)
- Write an ADR for every significant technical decision
- Store ADRs in `docs/adr/` directory
- Follow the template in `docs/adr/README.md`
- Update ADR index when adding new records

## Tech Stack
- Java 25 LTS
- Spring Boot 4.x (Spring Framework 7)
- PostgreSQL 16.x (RDS)
- Gradle 9.x with Kotlin DSL
- Liquibase for database migrations
- Terraform for infrastructure
- AWS ECS Fargate for deployment

## Project Structure
- Modular monolith architecture
- Separate schemas per module for data isolation
- Fraud module deployed as separate service
- Base package: `me.karun.bank.credit`
- See GitHub Wiki for architecture details

## GitHub Workflow
- Epics tracked as GitHub issues with `epic` label
- Stories tracked as GitHub issues with `story` label
- Link stories to parent epics
- Use module labels (e.g., `module:customer`)

## Ubiquitous Language

Use terminology from `docs/context/glossary.md` consistently across all documentation and code. This maintains a shared language between business and technical domains (per Domain-Driven Design principles).

When writing documentation or code:
- Prefer glossary terms over generic alternatives
- If a term isn't in the glossary but should be, add it
- First use of a term with an abbreviation: "Full Term (ABBR)"

## Context Documentation

Documentation optimized for intelligent Engineering (iE) tools like Claude Code. Located in `docs/context/`.

### Structure
| File | Purpose |
|------|---------|
| `overview.md` | System purpose, architecture summary |
| `glossary.md` | Domain terms and definitions |
| `domain/<name>.md` | Business rules, entity lifecycles |
| `modules/<name>.md` | Module boundaries, APIs, data owned |
| `integrations.md` | External systems and APIs |
| `conventions.md` | Code patterns and standards |
| `testing.md` | TDD principles, test strategy, tooling |
| `current-state.md` | What's built vs planned |

### When Learning About the System
When developers ask about the system, domain, or architecture:
1. Start with `docs/context/overview.md` for high-level understanding
2. Check `docs/context/glossary.md` for domain terminology
3. Read relevant files in `docs/context/domain/` for business rules
4. Read relevant files in `docs/context/modules/` for technical details
5. Check `docs/context/current-state.md` for what's implemented vs planned

### When Completing Stories
After completing a story, **always check if context docs need updating**:
1. Review what changed in the story
2. Check the "Context Docs to Update" section in the story
3. Update relevant context docs atomically with code changes
4. If uncertain, run `/update-context` to review systematically

### Definition of Done
A story is not complete until:
- Code is implemented and tested
- Context docs are updated if the story changed any of:
  - Domain rules or entity lifecycles
  - Module boundaries or public APIs
  - System architecture or integrations
  - Code conventions or patterns
  - What's built/planned (always update `current-state.md` for new features)
