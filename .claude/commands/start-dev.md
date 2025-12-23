# Start Development

You are helping the user develop a story using Test-Driven Development (TDD).

**Before starting:** Read `docs/context/testing.md` for TDD principles and `docs/context/conventions.md` for code standards.

## Phase 1: Card Identification

1. **Get current user and their assigned cards**:
   ```bash
   gh api user --jq .login
   gh issue list --state open --label story --assignee @me --json number,title,body,labels --limit 10
   ```

2. **Handle assignment state**:
   - If **no cards assigned**: Stop and tell user to run `/pickup` first
   - If **one card assigned**: Proceed with that card
   - If **multiple cards assigned**: Use AskUserQuestion to let user choose which to work on

3. **Ensure main is up to date** (trunk-based development):
   ```bash
   git checkout main && git pull
   ```

## Phase 2: Design Discussion

**Goal**: Build consensus on the approach before writing any code.

1. **Read the full issue** using `gh issue view <number>`

2. **Review relevant context docs**:
   - `docs/context/domain/` for business rules
   - `docs/context/modules/` for module boundaries
   - `docs/context/conventions.md` for code standards
   - `docs/context/testing.md` for test strategy

3. **Present your understanding**:
   - Summarize what the story is asking for
   - Identify key design decisions that need to be made
   - Propose an approach with specific technical choices
   - List the acceptance criteria as a checklist

4. **Have a conversation**:
   - Challenge assumptions, ask clarifying questions
   - Discuss trade-offs openly
   - The user may push back - this is good, engage with their concerns
   - **Do NOT proceed to coding until there is clear agreement on the approach**

5. **If blocked by unclear requirements**:
   - Use AskUserQuestion to present options
   - Give user the choice of: clarify now, create a spike, or de-scope

## Phase 3: Test-Driven Development

**Delegate to TDD skill** for Red-Green-Refactor workflow.

The TDD skill handles:
- Writing failing tests (RED)
- Making tests pass (GREEN)
- Cleaning up code (REFACTOR)
- Committing changes

### Review Mode

Default is **interactive** (user reviews each cycle). User can change mode:

| Mode | Command | Behavior |
|------|---------|----------|
| Interactive | `use interactive` | Review each Red-Green cycle |
| Batch AC | `use batch-ac` | Review after each acceptance criterion |
| Batch Story | `use batch-story` | Review after all criteria complete |
| Autonomous | `use autonomous [strict/normal/relaxed]` | Agent reviews with threshold |

### Working Through Acceptance Criteria

For each acceptance criterion:
1. Follow TDD skill workflow (Red-Green-Refactor cycles)
2. At review point (based on mode), get user feedback
3. Address feedback before moving to next criterion
4. Mark criterion complete when all tests pass and user approves

## Phase 4: Completion

When **all acceptance criteria are met**:

1. **Run full build**:
   ```bash
   ./gradlew build
   ```

2. **Update context documentation** (check the story's "Context Docs to Update" section):
   - Update `docs/context/current-state.md` to reflect what's now built
   - Update any domain or module docs if behavior changed
   - Commit documentation updates

3. **Final commit with Closes footer**:
   The last commit should include `Closes #<issue-number>` in the footer to auto-close the story when pushed.

4. **Push to main** (trunk-based development):
   ```bash
   git push origin main
   ```
   Story auto-closes via the `Closes #N` footer.

5. **Report completion** to the user

## Handling Blockers

If you discover a blocker (missing dependency, unclear requirement, technical issue):

**STOP** and use AskUserQuestion to present options:
- Clarify the requirement now
- Create a separate story/task for the blocker
- De-scope and document as a limitation
- Something else (let user specify)

**Never proceed with assumptions when blocked. Give the user the choice.**

## Key Reminders

- **No code without a failing test first** - non-negotiable
- **Tests must actually run** - "this would fail" doesn't count
- **Small steps** - each test covers one small piece
- **Conversation is key** - challenge each other
- **When unsure, ask** - don't proceed without clarity
