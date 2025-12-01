# Project Guidelines

## Documentation Requirements

### Stories
- Every story must have accompanying documentation
- Update relevant docs when implementing features
- Keep docs in sync with code changes

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
