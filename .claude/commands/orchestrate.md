---
description: Start the development loop — create an agent team, dispatch ready work, then guide human through reviews, unblocking, and grooming
---

# Orchestrate

You are running the development control loop. This is the single command that drives all work forward.

This command uses **Agent Teams** for dispatch. Each worker is a named teammate with its own worktree, running as part of a coordinated team. You (the lead session) stay free to groom, review, and unblock while teammates develop.

## Phase 1: Auto-Dispatch

Find all unassigned stories with the `dispatch-ready` label and dispatch workers for them.

```bash
gh issue list --state open --label story --label dispatch-ready --json number,title,labels,assignees --limit 50 --jq '.[] | select(.assignees | length == 0)'
```

If nothing to dispatch, skip to Phase 2.

### 1a. Create the team

```
TeamCreate(team_name: "sprint-work", description: "Developing dispatch-ready stories")
```

### 1b. For each unassigned `dispatch-ready` issue:

1. Get full details: `gh issue view <number> --json number,title,body,labels,comments`
2. Verify it has acceptance criteria (body contains "Acceptance Criteria"). If not, skip and warn.
3. Assign: `gh issue edit <number> --add-assignee @me`
4. Add in-progress label: `gh issue edit <number> --add-label "in-progress"`
5. Derive branch name: `work/<number>-<slugified-title>` (lowercase, hyphens, max 50 chars)
6. Create a task in the shared task list:
   ```
   TaskCreate(subject: "#<number>: <title>", description: "Branch: <branch-name>\n\n<acceptance-criteria-summary>")
   ```
7. Spawn a teammate using the worker agent definition:

```
Agent(
  team_name: "sprint-work",
  name: "worker-<number>",
  isolation: "worktree",
  run_in_background: true,
  prompt: """
You have been assigned GitHub Issue #$NUMBER: $TITLE
Your name in this team is "worker-$NUMBER". The lead session name is "lead".

## Issue Details

$FULL_ISSUE_BODY

## Previous Review Feedback

$COMMENTS_IF_ANY

## Your Task

Task ID: $TASK_ID (claim it with TaskUpdate)
Develop the work described in this issue. Your worktree branch is `$BRANCH_NAME`.
Reference GitHub Issue #$NUMBER in your commit messages.

When you are finished and all tests pass:
1. Comment on the issue: gh issue comment $NUMBER --body "Development complete. Branch: $BRANCH_NAME. Ready for human review."
2. Update labels: gh issue edit $NUMBER --add-label "review:pending" --remove-label "in-progress" --remove-label "dispatch-ready"
3. Message the lead: SendMessage(to: "lead", summary: "Done with #$NUMBER", message: "Development complete for #$NUMBER. Branch: $BRANCH_NAME. Ready for review.")
4. Mark task complete: TaskUpdate(id: $TASK_ID, status: "completed")

Do NOT merge to main. The human will review your worktree and merge.

If you get stuck, message the lead before giving up:
  SendMessage(to: "lead", summary: "Stuck on #$NUMBER", message: "<explain what's blocking>")

If the lead sends you rejection feedback, iterate on the work (don't give up — fix and resubmit).
"""
)
```

### 1c. Report dispatch summary

| Issue | Title | Branch | Teammate | Status |
|-------|-------|--------|----------|--------|
| #N | ... | work/N-... | worker-N | Dispatched |

## Phase 2: Review Queue

Check for completed work waiting for review:

```bash
gh issue list --state open --label review:pending --json number,title --limit 20
```

Also run `git worktree list` to cross-reference branches.

If there are items to review, run the `/review-worktree` flow for each one. After each review (approve/reject/skip), continue to the next.

**On rejection:** If the teammate is still running (check the team task list), use `SendMessage` to send feedback directly. The teammate iterates in place — no re-dispatch needed. If the teammate has exited, fall back to the label-based re-dispatch (add `dispatch-ready`, feedback in issue comment).

If nothing to review, say so and move on.

## Phase 3: Stuck Agents

Check for workers that need help:

```bash
gh issue list --state open --label needs-help --json number,title --limit 20
```

Teammates will also message the lead directly when stuck. Check for both.

For each stuck issue:
1. Show the issue and the stuck comment/message
2. Ask the human how to unblock it (clarify requirements, close as won't-do, re-scope, etc.)
3. If unblocked and teammate is still running: `SendMessage` the guidance directly
4. If unblocked and teammate has exited: remove `needs-help` and add `dispatch-ready` for re-dispatch

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
   - Offer to dispatch it immediately by spawning a new teammate into the existing team

## Phase 5: Summary

Report the session summary:

| Category | Count | Details |
|----------|-------|---------|
| Dispatched | N | New teammates spawned |
| Reviewed | N | Approved: X, Rejected: Y, Skipped: Z |
| Unblocked | N | Stuck agents helped |
| Groomed | N | Stories made dispatch-ready |
| In Progress | N | Teammates currently running |

Remind the human they can:
- Run `/orchestrate` again later to check progress
- Run `/review-worktree` to review completed work
- Check teammate status via the shared task list (`Ctrl+T`)
- Send messages to teammates directly via `SendMessage`
- Clean up the team when done: `TeamDelete`
