# Stop 6: The Workflow

## The Full Cycle

Here's how a story goes from backlog to shipped code:

```
/pickup      → Choose and assign yourself a prioritized story
/start-dev   → Design discussion, then TDD implementation
             → Small commits after each Red-Green-Refactor cycle
             → Update context docs
git push     → Ship to main (trunk-based development)
```

## Step 1: Pick Up a Story

```bash
/pickup
```

This command:
1. Fetches open stories from GitHub Issues
2. Sorts by priority (P0 → P1 → P2)
3. Shows what's available vs assigned
4. Assigns your chosen story to you

No sprint planning meetings. No Jira board shuffling. Just pick and go.

## Step 2: Start Development

```bash
/start-dev
```

This command kicks off a structured process:

### Phase 1: Design Discussion
- Read the story and acceptance criteria
- Review relevant context docs
- Discuss the approach with AI
- **Get agreement before writing any code**

### Phase 2: TDD Implementation
For each acceptance criterion:
1. **RED**: Write a failing test
2. **GREEN**: Write minimal code to pass
3. **REFACTOR**: Clean up while tests stay green
4. **COMMIT**: Small, focused commit

### Phase 3: Completion
- Run full build
- Update context documentation
- Final commit with `Closes #N` to auto-close the story
- Push to main

## The Review Modes

You control how much oversight:

| Mode | Command | When AI Pauses |
|------|---------|----------------|
| Interactive | `use interactive` | After each Red-Green cycle |
| Batch AC | `use batch-ac` | After each acceptance criterion |
| Batch Story | `use batch-story` | After all criteria complete |
| Autonomous | `use autonomous` | AI reviews itself (with strictness threshold) |

Start with Interactive. Move to Batch as you build trust.

## Trunk-Based Development

This project uses trunk-based development:
- No long-lived feature branches
- Push directly to main
- Small, frequent commits
- If something breaks, revert is easy (because commits are small)

## The Slash Commands

| Command | Purpose |
|---------|---------|
| `/pickup` | Assign yourself a story |
| `/start-dev` | Begin TDD development |
| `/update-context` | Update docs after changes |
| `/check-drift` | Verify docs match code |
| `/tour` | This tour |

## See It In Action

[![Watch: Codifying Engineering Culture](https://img.youtube.com/vi/oK0N7pQ5rIY/maxresdefault.jpg)](https://www.youtube.com/watch?v=oK0N7pQ5rIY&list=PLY67XcOB0u1QhUHMtg9C1ddx8CO2FAf8I)

---

**Next:** [Stop 7: Try It Yourself](07-try-it.md) - Your turn
