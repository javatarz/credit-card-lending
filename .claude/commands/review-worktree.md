---
description: Review completed work in git worktrees and approve, reject, or inspect further
---

# Review Worktree

You are helping the human review work completed by dispatched worker agents. Follow these steps.

## Step 1: Find worktrees with pending reviews

Run `git worktree list` to see all active worktrees.

Also run `gh issue list --label review:pending --json number,title,assignees --limit 20` to find issues marked as ready for review. Cross-reference the branch names to match worktrees to issues.

Present the list to the human:

| # | Issue | Title | Branch | Worktree Path |
|---|-------|-------|--------|---------------|

If no worktrees have pending reviews, say so and stop.

Ask the human which worktree to review (by number or issue number).

## Step 2: Show the overview

For the selected worktree, show:

### Issue details
Run `gh issue view <number>` and display the issue description and acceptance criteria.

### Commit history
```bash
git -C <worktree-path> log main..HEAD --oneline
```

### Change summary
```bash
git -C <worktree-path> diff main --stat
```

Show all three to the human in a clear format.

## Step 3: Offer review options

Present these options:

1. **View full diff** — Run `git -C <worktree-path> diff main` and display it
2. **View specific file** — Ask which file, then show `git -C <worktree-path> diff main -- <file>`
3. **Run tests** — Run `cd <worktree-path> && ./gradlew test` and report results
4. **Run code review agent** — Spawn a read-only subagent to review the diff (see below)
5. **Approve** — Merge to main and close the issue
6. **Reject** — Reopen the issue with feedback
7. **Skip** — Move on, come back later

### Option: Code review agent

If the human requests a code review, spawn a read-only subagent with this prompt:

```
Review the git diff for the worktree at `$WORKTREE_PATH` against main.

Run: git -C $WORKTREE_PATH diff main

Evaluate:
- Code quality and correctness
- Test coverage (are new behaviors tested?)
- Adherence to project conventions (read docs/context/conventions.md first)
- Scope adherence (compare against the issue description below)

Issue description:
$GH_ISSUE_VIEW_OUTPUT

Provide your review as:
- **Issues** (things that should be fixed before merging)
- **Suggestions** (improvements that aren't blockers)
- **Looks good** (things done well)
```

The subagent should only have read-only tools: Read, Glob, Grep, Bash. No Write or Edit.

Present the review results to the human, then return to the options menu.

### Option: Approve

When the human approves:

```bash
# Get the current branch name from the worktree
cd <worktree-path>
BRANCH=$(git rev-parse --abbrev-ref HEAD)

# Switch to main and merge
cd <main-repo-path>
git merge $BRANCH --no-ff -m "Merge $BRANCH: <issue-title> (#<number>)"

# Close the GitHub Issue
gh issue close <number> --comment "Merged via worktree review."

# Clean up labels
gh issue edit <number> --remove-label "review:pending" --remove-label "in-progress"

# Clean up the worktree
git worktree remove <worktree-path>
```

Confirm each step to the human as it completes.

### Option: Reject

When the human rejects, ask for feedback text explaining what needs to change. Then:

```bash
# Add rejection feedback as a comment
gh issue comment <number> --body "Review rejected: $FEEDBACK"

# Update labels
gh issue edit <number> --remove-label "review:pending" --add-label "in-progress"
```

Tell the human the issue is back in the backlog. The feedback comment will be visible to the next worker when the issue is re-dispatched.

Do NOT delete the worktree on rejection. The next worker may want to see the existing work as a starting point.

## Step 4: Continue or finish

After handling one worktree, ask if the human wants to review another. If yes, return to Step 1. If no, show a summary of actions taken this session:

| Issue | Action | Details |
|-------|--------|---------|
| #N | Approved | Merged to main |
| #M | Rejected | Feedback: "..." |
| #K | Skipped | Still pending |
