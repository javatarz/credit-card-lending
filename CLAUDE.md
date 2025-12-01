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
