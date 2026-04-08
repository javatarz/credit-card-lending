---
name: sync-beads
description: Sync Beads cards with GitHub Issues. Closes Beads cards whose linked GitHub issue is closed. Creates Beads cards for new open GitHub story issues not yet tracked.
allowed-tools: Bash, Read
---

# Sync Beads with GitHub Issues

One-way sync: GitHub Issues is the source of truth. This skill brings Beads cards in line with GitHub.

## When to Use

- Before starting work (ensure backlog is current)
- After closing GitHub issues outside of Beads
- Periodically to catch drift

## Sync Process

### Step 1: Close stale Beads cards

Find open Beads cards linked to closed GitHub issues and close them.

```bash
# Get all open Beads cards with gh: labels
bd list --json 2>&1
```

For each open card, extract the GitHub issue number from the `gh:#<number>` label or `gh-<number>` external ref. Then check if that GitHub issue is closed:

```bash
gh issue view <number> --json state --jq '.state'
```

If the GitHub issue state is `CLOSED`, close the Beads card:

```bash
bd close <card-id>
```

Report which cards were closed and why.

### Step 2: Create cards for new GitHub stories

Find open GitHub story issues that don't have a corresponding Beads card.

```bash
# Get open story issues
gh issue list --state open --label story --json number,title,labels --limit 100 --jq '.[] | "\(.number)|\(.title)|\(.labels | map(.name) | map(select(startswith("module:"))) | .[0] // "")|\(.labels | map(.name) | map(select(test("^P[0-3]$"))) | .[0] // "P2")"'
```

For each issue, check if a Beads card already exists with that external ref:

```bash
bd list --json 2>&1 | grep "gh-<number>"
```

If no card exists, create one:

```bash
bd create --title "<title>" --external-ref "gh-<number>" --priority "<priority>" --labels "gh:#<number>,<module if present>" --silent
```

### Step 3: Report

Summarize what changed:
- Cards closed (with GitHub issue reference)
- Cards created (with GitHub issue reference)
- Total open cards remaining

## Rules

- Never modify GitHub Issues from this skill — GitHub is the source of truth
- Never close a Beads card unless the linked GitHub issue is confirmed closed
- Use `--silent` flag when creating cards to keep output clean
- If `bd list --json` format changes, adapt by checking `bd list --json --help` or inspecting output
