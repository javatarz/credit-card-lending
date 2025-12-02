# Update Context Documentation

You are helping the user review and update context documentation after completing work.

## Instructions

1. **Check recent changes and closed stories** by running:
   ```bash
   git log --oneline -5
   git diff --name-only HEAD~5
   gh issue list --state closed --label story --search "sort:updated-desc" --json number,title,closedAt --limit 5
   ```

2. **Cross-reference commits with closed stories**:
   - For each recently closed story, check if there's a corresponding commit
   - For each story, review what was delivered (use `gh issue view <number>`)
   - Identify any gaps where a story was completed but context docs weren't updated

3. **Review what was changed** and determine which context docs might need updates:

   | If you changed... | Consider updating... |
   |-------------------|---------------------|
   | New domain entity or rules | `docs/context/domain/<name>.md` |
   | New module or module boundaries | `docs/context/modules/<name>.md` |
   | External integration | `docs/context/integrations.md` |
   | New patterns or conventions | `docs/context/conventions.md` |
   | Architecture decisions | `docs/context/overview.md` |
   | New domain terms | `docs/context/glossary.md` |
   | New features built | `docs/context/current-state.md` |

3. **Present findings to user** using AskUserQuestion:
   - List what changed
   - Suggest which context docs might need updates
   - Ask user to confirm which docs to update

4. **For each doc to update**:
   - Read the current content
   - Propose specific changes based on recent work
   - Make edits after user approval

5. **After updates**, remind user to:
   - Commit context doc changes with the related code
   - Check that `current-state.md` reflects what's now built

## Context Doc Purposes

- **overview.md**: System architecture, module structure, tech stack summary
- **glossary.md**: Domain terms (credit, lending, customer lifecycle)
- **domain/*.md**: Business rules, entity states, lifecycle flows
- **modules/*.md**: Module responsibilities, APIs, database schemas
- **integrations.md**: External systems, API contracts
- **conventions.md**: Code patterns, naming, testing standards
- **current-state.md**: What's built vs planned, MVP scope

## Example Flow

```
User runs /update-context after implementing customer registration

1. Check git log and closed stories
2. Cross-reference: Story #21 closed, commit exists, but context not updated
3. Present: "I see story #21 (Customer Registration) was completed. Consider updating:
   - docs/context/domain/customer.md (if business rules changed)
   - docs/context/modules/customer-module.md (if APIs changed)
   - docs/context/current-state.md (to mark feature as built)"
4. User confirms which to update
5. Read each doc, propose changes, apply after approval
6. Remind to commit with code changes
```

## Catching Up on Missed Updates

If you find stories that were closed without context updates:
1. Review each story's acceptance criteria and tech notes
2. Compare against current context docs
3. Propose updates to bring docs in sync with what's actually built
4. This is especially important for `current-state.md` which should reflect reality

## Important

- Don't create new domain/module docs unless a new domain or module was added
- Keep updates focused and concise
- Context docs should be factual and dense, not narrative
- Always update `current-state.md` when features move from "planned" to "built"
