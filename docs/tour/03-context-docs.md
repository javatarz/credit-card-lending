# Stop 3: Context Docs

## What Are Context Docs?

The `docs/context/` directory contains documentation optimized for LLM consumption. Unlike traditional docs written for humans to read linearly, these are:

- **Dense**: Facts over prose, tables over paragraphs
- **Interconnected**: Clear links between related concepts
- **Current**: Updated as part of every story's definition of done
- **Structured**: Consistent format so AI knows where to find things

## Look At This

Open `docs/context/README.md` - it's the entry point.

Notice the structure:
```
docs/context/
├── README.md           # Navigation guide
├── overview.md         # System architecture, tech stack
├── conventions.md      # Code patterns and standards
├── testing.md          # TDD principles
├── current-state.md    # What's built vs planned
├── glossary.md         # Ubiquitous language
├── domain/             # Business rules by domain
└── modules/            # Technical boundaries by module
```

## Why This Matters

When you ask Claude Code a question like "How does SSN encryption work?", it:
1. Searches the context docs for relevant information
2. Finds `docs/context/modules/customer-module.md` which documents the encryption approach
3. Can follow links to related concepts (compliance, the Customer entity)
4. Gives you a grounded answer

Without these docs, AI responses are generic. With them, responses are specific to *your* system.

## The Pattern

Each context doc follows a pattern:

```markdown
# Title

## Why Read This?
One sentence on when you'd need this doc.

**Prerequisites:** Links to docs you should read first
**Related:** Links to related docs

---

[Content organized in tables and bullet points]
```

## Try It

1. Open `docs/context/overview.md`
2. Notice how it uses tables for tech stack, bullet points for decisions
3. Compare to a traditional README - which would be easier for an AI to parse?

## Keeping Docs Current

Every story template includes:
```markdown
## Context Docs to Update
- [ ] Which docs need updating based on this story's changes
```

This makes documentation a first-class deliverable, not an afterthought.

---

**Next:** [Stop 4: CLAUDE.md](04-claude-md.md) - Project instructions that shape AI behavior
