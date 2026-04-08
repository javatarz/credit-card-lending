---
description: Find ready cards from the Beads backlog and dispatch worker agents to develop them in isolated worktrees
---

# Dispatch Workers

You are dispatching work from the Beads backlog to worker agents. Follow these steps exactly.

## Step 1: Find ready work

Run `bd ready --plain` to get the list of unblocked cards available for work.

If no cards are ready, tell the human and stop. Suggest they groom cards or check `bd list` to see what's blocked.

## Step 2: Present cards for selection

Show the human the list of ready cards. For each card, display:
- ID
- Title
- Priority (if set)
- Labels (especially module labels)

Ask the human which cards to dispatch. Accept a comma-separated list of IDs, "all", or let them pick interactively.

## Step 3: Dispatch a worker for each selected card

For each selected card:

### 3a. Get card details

Run `bd show <id>` to get the full card description, acceptance criteria, and any notes (including rejection feedback from previous attempts).

### 3b. Claim the card

Run `bd update <id> --claim` to atomically set the card to in_progress and assign it.

If claiming fails (card already claimed), skip it and tell the human.

### 3c. Create a worktree branch name

Derive a branch name from the card ID and title. Format: `work/<card-id>-<slugified-title>` (lowercase, hyphens, max 50 chars total).

Example: `work/bd-a1b2-add-payment-validation`

### 3d. Look up the GitHub Issue number

If the card has a `gh:#<number>` label, extract the issue number. The worker needs this for commit messages.

### 3e. Spawn the worker agent

Use the Agent tool to spawn a `worker` agent with the following prompt. Substitute the actual values for the placeholders:

```
You have been assigned card **$CARD_ID**: $CARD_TITLE

## Card Details

$FULL_BD_SHOW_OUTPUT

## GitHub Issue

This card corresponds to GitHub Issue #$GH_ISSUE_NUMBER. Reference this in your commit messages.

## Your Task

Develop the work described in this card. Your worktree branch is `$BRANCH_NAME`.

When you are finished and all tests pass, run:

```
bd update $CARD_ID --status blocked --add-label review:pending
bd note $CARD_ID "Development complete. Branch: $BRANCH_NAME. Ready for human review."
```

Do NOT merge to main. The human will review your worktree and merge.
```

Pass `isolation: "worktree"` and `run_in_background: true` so the agent gets its own working directory and doesn't block this session.

### 3f. Track what was dispatched

Keep a record of each dispatch: card ID, branch name, and the spawned agent.

## Step 4: Report

After all workers are spawned, report a summary table:

| Card | Title | Branch | Status |
|------|-------|--------|--------|
| bd-xxxx | ... | work/bd-xxxx-... | Dispatched |

Tell the human they can:
- Continue working in this session while workers run
- Check progress with `bd list --label review:pending`
- Review completed work with `/review-worktree`
