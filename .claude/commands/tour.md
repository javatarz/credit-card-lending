# Learning Tour

You are guiding the user through a tour of this repository and the intelligent Engineering (iE) approach.

## Instructions

### If No Argument Provided

Show the tour menu:

```markdown
## Learning Tour

Welcome! This tour teaches you how to use AI-assisted development with this repository.

| Stop | Title | Description |
|------|-------|-------------|
| 1 | Welcome | What this repo is, what you'll learn |
| 2 | Onboard With AI | Ask questions about the codebase right now |
| 3 | Context Docs | LLM-optimized docs that enable AI collaboration |
| 4 | CLAUDE.md | Project instructions that shape AI behavior |
| 5 | A Real Commit | Walk through an actual TDD commit |
| 6 | The Workflow | From picking a story to pushing code |
| 7 | Try It Yourself | Your turn to practice |

Run `/tour <number>` to go to a specific stop, or say "start tour" to begin from Stop 1.

**Prefer reading?** See `docs/tour/` for the standalone markdown version.
```

Use AskUserQuestion to let them choose a stop or start from the beginning.

### If Argument Provided

The argument is the stop number (1-7). Present that stop's content interactively.

## Tour Content

### Stop 1: Welcome

**Key points to cover:**
- This is a credit card lending platform built entirely with AI assistance
- Every commit was created with Claude Code using TDD
- The domain is complex (PCI, fraud, billing) - if it works here, it works for you
- Tour teaches: documentation structure, TDD with AI, the workflow, AI-powered onboarding

**Files to reference:** `README.md`

**End with:** "Ready for Stop 2? Say 'next' or run `/tour 2`"

### Stop 2: Onboard With AI

**Key points to cover:**
- Onboarding is now a conversation, not document reading
- Right now, in this session, they can ask questions about the codebase
- This works because of context docs, CLAUDE.md, and wiki

**Demonstrate by inviting them to ask:**
- "What is the Customer module responsible for?"
- "How does SSN encryption work in this system?"
- "What's the difference between a Customer and an Application?"
- "Where would I add a new validation rule for credit applications?"

**Important:** If they ask a question, answer it! This stop is interactive.

**Files to reference:** `docs/context/README.md`

**End with:** "The next stop explains how these docs are structured. Say 'next' or run `/tour 3`"

### Stop 3: Context Docs

**Key points to cover:**
- `docs/context/` contains LLM-optimized documentation
- Dense facts, tables over prose, interconnected
- Updated as part of every story's definition of done
- This is what enables Stop 2 to work

**Files to reference:** `docs/context/README.md`, `docs/context/overview.md`

**End with:** "Next we'll look at CLAUDE.md - the project instruction file. Say 'next' or run `/tour 4`"

### Stop 4: CLAUDE.md

**Key points to cover:**
- CLAUDE.md is read automatically by Claude Code
- Contains project-specific rules: git workflow, conventions, skills
- Two layers: global (~/.claude/CLAUDE.md) and project (./CLAUDE.md)
- Skills (TDD, Review) activate automatically based on context

**Files to reference:** `CLAUDE.md`, `.claude/skills/tdd/SKILL.md`

**End with:** "Let's look at a real commit from this repo. Say 'next' or run `/tour 5`"

### Stop 5: A Real Commit

**Key points to cover:**
- Examine commit `0974aa0` - EncryptionService implementation
- Part of Story #23 (Customer Profile Completion)
- Notice: tests included, small focused change, clear commit message
- The TDD pattern: test first, then implementation, then refactor

**Run this command to show the commit:**
```bash
git show 0974aa0 --stat
```

**Files to reference:** Show the commit diff or point to the files

**End with:** "Now let's see the full workflow. Say 'next' or run `/tour 6`"

### Stop 6: The Workflow

**Key points to cover:**
- `/pickup` → `/start-dev` → commit → push
- Design discussion before coding
- TDD with review modes (interactive, batch, autonomous)
- Trunk-based development - push to main
- Documentation updated as part of completion

**Reference the screencast:**
- [Codifying Engineering Culture](https://www.youtube.com/watch?v=oK0N7pQ5rIY&list=PLY67XcOB0u1QhUHMtg9C1ddx8CO2FAf8I)

**End with:** "Final stop - your turn to try it. Say 'next' or run `/tour 7`"

### Stop 7: Try It Yourself

**Key points to cover:**
- Options: pick up a real story, explore the codebase, apply to own project
- Suggest `good-first-issue` labeled stories
- Remind them they can ask questions anytime
- The meta point: this tour was built using the workflow it describes

**End with:**
```
Tour complete!

What would you like to do?
- `/pickup` - Grab a story and start contributing
- Ask a question about the codebase
- `/tour` - Revisit any stop
```

## Navigation

Always offer navigation options:
- "next" or "n" → Go to next stop
- "back" or "b" → Go to previous stop
- "menu" or "m" → Show the tour menu
- `/tour <number>` → Jump to specific stop

## Important Notes

- Be conversational, not scripty
- If they ask questions during a stop, answer them - that's the point
- Keep each stop focused - don't dump everything at once
- The markdown files in `docs/tour/` have more detail if they want to read offline
