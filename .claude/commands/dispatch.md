---
description: Find ready stories from GitHub Issues and dispatch worker agents to develop them in isolated worktrees
---

# Dispatch Workers

You are dispatching work from the GitHub Issues backlog to worker agents. Follow these steps exactly.

## Step 1: Find ready work

Fetch open stories with the `dispatch-ready` label:

```bash
gh issue list --state open --label story --label dispatch-ready --json number,title,body,labels,assignees --limit 50
```

Filter to unassigned issues. Sort by priority label: P0 first, then P1, P2, etc.

If no issues are ready, tell the human and stop.

## Step 2: Present issues for selection

Show the human the list of available issues. For each issue, display:
- Issue number
- Title
- Priority label
- Module labels
- Whether it has acceptance criteria (check if body contains "Acceptance Criteria")

Ask the human which issues to dispatch. Accept a comma-separated list of numbers, or let them pick interactively.

## Step 3: Dispatch a worker for each selected issue

For each selected issue:

### 3a. Get full issue details

```bash
gh issue view <number> --json number,title,body,labels,comments
```

Verify the issue has acceptance criteria. If not, warn the human and ask if they want to proceed anyway.

### 3b. Assign the issue

```bash
gh issue edit <number> --add-assignee @me
```

### 3c. Add in-progress label

```bash
gh issue edit <number> --add-label "in-progress"
```

### 3d. Create a worktree branch name

Derive a branch name from the issue number and title. Format: `work/<number>-<slugified-title>` (lowercase, hyphens, max 50 chars total).

Example: `work/49-auth-and-authorization`

### 3e. Spawn the worker agent

Use the Agent tool to spawn a worker agent with the following prompt. Substitute the actual values:

```
You have been assigned GitHub Issue #$NUMBER: $TITLE

## Issue Details

$FULL_ISSUE_BODY

## Previous Review Feedback

$COMMENTS_IF_ANY (especially look for rejection feedback from prior attempts)

## Your Task

Develop the work described in this issue. Your worktree branch is `$BRANCH_NAME`.

When you are finished and all tests pass:
1. Add a comment to the issue summarizing what you did:
   ```
   gh issue comment $NUMBER --body "Development complete. Branch: $BRANCH_NAME. Ready for human review."
   ```
2. Add the review:pending label:
   ```
   gh issue edit $NUMBER --add-label "review:pending" --remove-label "in-progress"
   ```

Do NOT merge to main. The human will review your worktree and merge.
```

Pass `isolation: "worktree"` and `run_in_background: true` so the agent gets its own working directory and doesn't block this session.

### 3f. Track what was dispatched

Keep a record of each dispatch: issue number, branch name, and the spawned agent.

## Step 4: Report

After all workers are spawned, report a summary table:

| Issue | Title | Branch | Status |
|-------|-------|--------|--------|
| #N | ... | work/N-... | Dispatched |

Tell the human they can:
- Continue working in this session while workers run
- Check progress with `gh issue list --label review:pending`
- Review completed work with `/review-worktree`
