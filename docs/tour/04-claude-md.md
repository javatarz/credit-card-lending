# Stop 4: CLAUDE.md

## What Is CLAUDE.md?

`CLAUDE.md` is a special file that Claude Code reads automatically when you start a session. It contains project-specific instructions that shape how the AI behaves in *this* codebase.

## Look At This

Open `CLAUDE.md` in the repository root.

You'll find:
- **Repository information** - Links to issues, wiki, where things live
- **Documentation requirements** - Where to put ADRs, context docs, etc.
- **Git workflow rules** - Commit message format, when to commit, behavioral rules
- **Development skills** - TDD enforcement, code review modes

## Why This Matters

Without CLAUDE.md, Claude Code gives generic responses. With it:

- Commit messages follow your team's format
- Code follows your conventions
- AI knows where to look for context
- Workflows match your process

It's like pair programming with someone who's read the contributing guide.

## Key Sections

### Git Workflow

```markdown
| Rule | Description |
|------|-------------|
| **Never commit without user confirmation** | Explain what will be committed |
| **Never push without asking** | Always confirm before pushing |
```

These rules prevent the AI from making changes you haven't approved.

### Skills

CLAUDE.md references skills (`.claude/skills/`) that activate based on context:

- **TDD Skill**: Enforces test-first development when writing code
- **Review Skill**: Assesses code quality with configurable strictness

### Context Management

The doc includes patterns for managing conversation context:
- When to use `NOTES.md` for persistent session info
- When to compact vs clear conversations
- Using `scratch/` for temporary work

## The Two-Layer Pattern

There are actually two CLAUDE.md files:
1. `~/.claude/CLAUDE.md` - Your personal global instructions (all projects)
2. `./CLAUDE.md` - Project-specific instructions (this repo)

Project instructions can override or extend global ones.

## Try It

1. Read through `CLAUDE.md`
2. Notice the "Conflict Resolution" section - what happens when rules conflict?
3. Look at the commit message format - how specific is it?

---

**Next:** [Stop 5: A Real Commit](05-real-commit.md) - See TDD + AI in action
