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

## Git Workflow

### Behavioral Rules

| Rule | Description |
|------|-------------|
| **Never commit without user confirmation** | Explain what will be committed and why, then wait for approval |
| **Never push without asking** | Always confirm before pushing to remote |
| **Smallest independent change** | Each commit should be the smallest change that doesn't break the system |
| **Prefer many small commits** | Over few large ones - easier to review and revert |

### Commit Message Format

```
<type prefix><subject> #N

<optional body - why, not how>

<optional: Closes #N>
```

| Rule | Description |
|------|-------------|
| **Imperative mood** | "Add feature" not "Added feature" |
| **Subject ≤ 50 chars** | Excludes issue reference |
| **Capitalize subject** | "Add validation" not "add validation" |
| **No trailing period** | Wastes space |
| **Reference the issue** | Append `#N` for traceability |
| **Close via footer** | Use `Closes #N` only in final commit to auto-close issue |

**Type prefixes:** `Add`, `Fix`, `Update`, `Remove`, `Refactor`, `Rename`, `Move`, `Docs`, `Test`, `Config`

### Squashing Workflow

Only squash unpushed commits that logically belong together:
```bash
git commit --fixup=<commit>
GIT_SEQUENCE_EDITOR=true git rebase -i --autosquash <commit>~1
```

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

## Development Skills

Model-invoked skills that activate automatically based on context.

### TDD Skill

Enforces Test-Driven Development when writing code.

| Aspect | Details |
|--------|---------|
| Activates | When implementing features, fixing bugs, writing code |
| Bypass | Say "quick fix" or "no tdd" |
| Location | `.claude/skills/tdd/SKILL.md` |

**Review Modes:**

| Mode | Command | When Reviews Happen |
|------|---------|---------------------|
| Interactive (default) | `use interactive` | After each Red-Green cycle |
| Batch AC | `use batch-ac` | After each acceptance criterion |
| Batch Story | `use batch-story` | After all criteria complete |
| Autonomous | `use autonomous [strict/normal/relaxed]` | Agent reviews continuously |

### Review Skill

Assesses code quality with configurable strictness.

| Aspect | Details |
|--------|---------|
| Activates | After code changes, on "review" requests, in TDD autonomous mode |
| Thresholds | `strict` (everything), `normal` (significant), `relaxed` (blockers only) |
| Location | `.claude/skills/review/SKILL.md` |

**Output:** Structured findings (Blockers, Warnings, Suggestions) with verdict (PASS/NEEDS ATTENTION/BLOCKED).
