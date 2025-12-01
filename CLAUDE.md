# Project Guidelines

## Documentation Requirements

### Stories
- GitHub issues are the source of truth for epics and stories
- All stories must follow the template below
- Include all sections even if not applicable (use "None" for empty sections)
- No separate documentation files for stories - keep everything in GitHub issues

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
- Java 21 LTS
- Spring Boot 3.2.x
- PostgreSQL 15.x (RDS)
- Gradle with Kotlin DSL
- Terraform for infrastructure
- AWS ECS Fargate for deployment

## Project Structure
- Modular monolith architecture
- Separate schemas per module for data isolation
- Fraud module deployed as separate service
- See `docs/ARCHITECTURE.md` for details

## GitHub Workflow
- Epics tracked as GitHub issues with `epic` label
- Stories tracked as GitHub issues with `story` label
- Link stories to parent epics
- Use module labels (e.g., `module:customer`)
