---
description: Find ready stories from GitHub Issues and dispatch worker teammates to develop them in isolated worktrees
---

# Dispatch Workers

You are dispatching work from the GitHub Issues backlog to worker teammates. This command uses **Agent Teams** — each worker is a named teammate with its own worktree, coordinated via a shared task list.

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

## Step 3: Create the team and dispatch teammates

### 3a. Create the team

If no team exists yet for this session:

```
TeamCreate(team_name: "sprint-work", description: "Developing dispatch-ready stories")
```

If a team already exists (e.g., from a prior `/orchestrate` or `/dispatch`), reuse it by passing the existing `team_name` when spawning teammates.

### 3b. For each selected issue:

#### Get full issue details

```bash
gh issue view <number> --json number,title,body,labels,comments
```

Verify the issue has acceptance criteria. If not, warn the human and ask if they want to proceed anyway.

#### Assign the issue

```bash
gh issue edit <number> --add-assignee @me
gh issue edit <number> --add-label "in-progress"
```

#### Create a branch name

Format: `work/<number>-<slugified-title>` (lowercase, hyphens, max 50 chars total).

#### Create a task in the shared list

```
TaskCreate(subject: "#<number>: <title>", description: "Branch: <branch-name>\n\n<acceptance-criteria-summary>")
```

#### Spawn the teammate

Use the Agent tool to spawn a worker teammate:

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

$COMMENTS_IF_ANY (especially look for rejection feedback from prior attempts)

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

If you get stuck, message the lead:
  SendMessage(to: "lead", summary: "Stuck on #$NUMBER", message: "<explain what's blocking>")

If the lead sends you rejection feedback, iterate on the work (fix and resubmit).
"""
)
```

## Step 4: Report

After all teammates are spawned, report a summary table:

| Issue | Title | Branch | Teammate | Status |
|-------|-------|--------|----------|--------|
| #N | ... | work/N-... | worker-N | Dispatched |

Tell the human they can:
- Continue working in this session while teammates develop
- Check progress via the shared task list (`Ctrl+T`)
- Review completed work with `/review-worktree`
- Send messages to teammates via `SendMessage`
