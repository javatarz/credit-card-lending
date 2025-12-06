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

Follow the **Red-Green-Refactor** cycle. See `docs/context/testing.md` for the full principles.

### The Workflow

For each piece of functionality:

#### RED: Write a Failing Test

1. Identify the next small piece of functionality
2. Write the test (just enough to fail)
3. **Show the test to the user** and explain what it's testing
4. **Wait for user feedback** - they may suggest changes
5. Run the test:
   ```bash
   ./gradlew test
   ```
6. **Confirm RED**: You MUST see the test fail
   - If it passes: STOP and discuss - this is suspicious

#### GREEN: Make it Pass

1. Choose a technique (Fake It, Obvious Implementation, or Triangulation - see testing.md)
2. Write minimum code to pass
3. **Show the implementation** to the user
4. Run the test:
   ```bash
   ./gradlew test
   ```
5. **Confirm GREEN**: You MUST see the test pass before proceeding

#### REFACTOR: Clean Up

1. Look for duplication (primary target)
2. **Suggest refactorings** if you see opportunities
3. **Ask the user**: "Any refactoring you'd like to do before we commit?"
4. **Trust the user's judgment** - if they say skip, skip
5. If refactoring: Run tests again to ensure still GREEN

**STOP: Wait for user confirmation before proceeding to commit.**

#### COMMIT

**Only after user confirms they are ready:**
```bash
git add -A && git commit -m "<descriptive message>

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

### Repeat

Continue until the current acceptance criterion is complete, then move to the next one.

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

3. **Push to main** (trunk-based development):
   ```bash
   git push origin main
   ```

4. **Close the story**:
   ```bash
   gh issue close <issue-number> --comment "Implemented and pushed to main.

   🤖 Generated with [Claude Code](https://claude.com/claude-code)"
   ```

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
