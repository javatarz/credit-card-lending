# Project Guidelines

> **Conflict Resolution:** If any rules in this file or `docs/context/` conflict with global instructions (`~/.claude/CLAUDE.md`), ask the user for clarification before proceeding.

## Repository Information
- **Never guess or hallucinate repository URLs**
- Always run `git remote -v` to get the actual repository URL
- Repository: `javatarz/credit-card-lending` (verified via git remote)
- GitHub Issues: https://github.com/javatarz/credit-card-lending/issues
- GitHub Wiki: https://github.com/javatarz/credit-card-lending/wiki

## Documentation Requirements

### Stories & Epics
- GitHub issues are the source of truth for epics and stories
- Every change must have a corresponding GitHub issue
- **Story template:** [`docs/templates/story.md`](docs/templates/story.md) - use exactly as written

### Documentation Locations
| Type | Location | Purpose |
|------|----------|---------|
| ADRs | `docs/adr/` (repo) | Technical decisions tied to code |
| Context docs | `docs/context/` (repo) | iE-optimized system documentation |
| Architecture | GitHub Wiki | System design, module overview |
| API docs | Swagger (auto) | Endpoint documentation |
| Epics/Stories | GitHub Issues | Requirements and tracking |

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

See [`docs/context/overview.md`](docs/context/overview.md) for the canonical tech stack reference.

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

iE-optimized documentation in `docs/context/`. See [`docs/context/README.md`](docs/context/README.md) for navigation and structure.

### Definition of Done
A story is not complete until:
- Code is implemented and tested
- Context docs are updated if the story changed domain rules, module APIs, architecture, or conventions

## Wiki Access

The GitHub Wiki contains architecture and design documentation. Access it via the `wiki` skill.

| Aspect | Details |
|--------|---------|
| Local path | `docs/wiki/` (gitignored) |
| Online | GitHub Wiki (see repo) |
| Access | Automatic via wiki skill |

**Reading:** Just ask about architecture, design, etc. Auto-clones and auto-pulls.

**Writing:** Edit locally, then ask to push (requires approval).

**Fallback:** If clone fails, skill provides online wiki URL.
