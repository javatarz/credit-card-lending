# Context Documentation

Documentation optimized for intelligent Engineering (iE) tools like Claude Code.

## Quick Navigation

### By Goal

| I want to... | Start here |
|--------------|------------|
| Understand the system | [overview.md](overview.md) → [glossary.md](glossary.md) |
| Learn the domain | [glossary.md](glossary.md) → [domain/customer.md](domain/customer.md) |
| Write code | [conventions.md](conventions.md) → [testing.md](testing.md) |
| Add a new module | [modules/README.md](modules/README.md) → existing examples |
| Add a new domain | [domain/README.md](domain/README.md) → existing examples |
| Check what's built | [current-state.md](current-state.md) |
| Integrate external systems | [integrations.md](integrations.md) |

### File Index

| File | Purpose | Read when... |
|------|---------|--------------|
| [overview.md](overview.md) | Architecture, tech stack, module boundaries | Starting on the project |
| [glossary.md](glossary.md) | Domain terminology | You encounter unfamiliar terms |
| [conventions.md](conventions.md) | Code style, naming, git workflow | Writing or reviewing code |
| [testing.md](testing.md) | TDD approach, test strategy, tooling | Writing tests |
| [integrations.md](integrations.md) | External APIs and systems | Working with external services |
| [current-state.md](current-state.md) | What's built vs planned | Planning work or checking scope |
| [domain/](domain/) | Business rules per domain | Implementing domain logic |
| [modules/](modules/) | Technical details per module | Working within a module |

## Document Structure

Each context file follows this structure:

```markdown
# Title

## Why Read This?
[Purpose statement]

**Prerequisites:** [docs to read first]
**Related:** [related docs]

## Content...
```

## Keeping Docs Updated

When completing a story, check if context docs need updating:
1. Review what changed
2. Update relevant docs atomically with code
3. If uncertain, ask: "Did this change domain rules, module APIs, or architecture?"

See [CLAUDE.md](../../CLAUDE.md) for the full "Context Docs to Update" checklist.
