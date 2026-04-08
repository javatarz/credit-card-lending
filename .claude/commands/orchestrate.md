---
description: Start the development loop — auto-dispatch ready work to agents, then guide human through reviews, unblocking, and grooming
---

# Orchestrate

You are running the development control loop. This is the single command that drives all work forward.

## Phase 1: Auto-Dispatch

Find all unassigned stories with the `dispatch-ready` label and spawn workers for them.

```bash
gh issue list --state open --label story --label dispatch-ready --json number,title,labels,assignees --limit 50 --jq '.[] | select(.assignees | length == 0)'
```

For each unassigned `dispatch-ready` issue:

1. Get full details: `gh issue view <number> --json number,title,body,labels,comments`
2. Verify it has acceptance criteria (body contains "Acceptance Criteria"). If not, skip and warn.
3. Assign: `gh issue edit <number> --add-assignee @me`
4. Add in-progress label: `gh issue edit <number> --add-label "in-progress"`
5. Derive branch name: `work/<number>-<slugified-title>` (lowercase, hyphens, max 50 chars)
6. Spawn a worker agent with `isolation: "worktree"` and `run_in_background: true`:

```
You have been assigned GitHub Issue #$NUMBER: $TITLE

## Issue Details

$FULL_ISSUE_BODY

## Previous Review Feedback

$COMMENTS_IF_ANY

## Your Task

Develop the work described in this issue. Your worktree branch is `$BRANCH_NAME`.
Reference GitHub Issue #$NUMBER in your commit messages.

When you are finished and all tests pass:
1. Comment on the issue: gh issue comment $NUMBER --body "Development complete. Branch: $BRANCH_NAME. Ready for human review."
2. Update labels: gh issue edit $NUMBER --add-label "review:pending" --remove-label "in-progress" --remove-label "dispatch-ready"

Do NOT merge to main. The human will review your worktree and merge.
```

Report a summary table of what was dispatched:

| Issue | Title | Branch | Status |
|-------|-------|--------|--------|
| #N | ... | work/N-... | Dispatched |

If nothing to dispatch, say so and move on.

## Phase 2: Review Queue

Check for completed work waiting for review:

```bash
gh issue list --state open --label review:pending --json number,title --limit 20
```

Also run `git worktree list` to cross-reference branches.

If there are items to review, run the `/review-worktree` flow for each one. After each review (approve/reject/skip), continue to the next.

If nothing to review, say so and move on.

## Phase 3: Stuck Agents

Check for workers that need help:

```bash
gh issue list --state open --label needs-help --json number,title --limit 20
```

For each stuck issue:
1. Show the issue and the stuck comment
2. Ask the human how to unblock it (clarify requirements, close as won't-do, re-scope, etc.)
3. If unblocked, remove `needs-help` and add `dispatch-ready` so it gets picked up next time

If nothing stuck, say so and move on.

## Phase 4: Groom Queue

Find stories that aren't yet `dispatch-ready`:

```bash
gh issue list --state open --label story --json number,title,labels --limit 50 --jq '[.[] | select(.labels | map(.name) | index("dispatch-ready") | not) | select(.labels | map(.name) | index("in-progress") | not) | select(.labels | map(.name) | index("review:pending") | not) | select(.labels | map(.name) | index("needs-help") | not)]'
```

If there are ungroomed stories:
1. Present them grouped by priority
2. Ask the human which to groom (or skip grooming)
3. For each selected story:
   - Show the current description and ACs
   - Identify issues (vague ACs, missing context, conflicts with existing work)
   - Suggest improvements
   - When the human is satisfied, add `dispatch-ready` label
   - Offer to dispatch it immediately by spawning a worker

## Phase 5: Summary

Report the session summary:

| Category | Count | Details |
|----------|-------|---------|
| Dispatched | N | New workers spawned |
| Reviewed | N | Approved: X, Rejected: Y, Skipped: Z |
| Unblocked | N | Stuck agents helped |
| Groomed | N | Stories made dispatch-ready |
| In Progress | N | Workers currently running |

Remind the human they can:
- Run `/orchestrate` again later to check progress
- Run `/review-worktree` to review completed work
- Check agent status with `gh issue list --label in-progress`
