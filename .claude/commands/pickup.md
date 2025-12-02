# Pick Up Next Card

You are helping the user pick up the next prioritized story to work on.

## Instructions

1. **Fetch open stories** using GitHub CLI:
   ```bash
   gh issue list --state open --label story --json number,title,labels,assignees,updatedAt --limit 100
   ```

2. **Filter and sort the cards**:
   - Exclude issues with the `epic` label (only stories)
   - Sort by priority: P0 first, then P1, then P2, then unprioritized
   - Within each priority, sort by issue number (lower = older = higher priority)

3. **Categorize cards by availability**:
   - **Available**: Unassigned cards
   - **Potentially Available**: Cards assigned to someone else BUT with no updates for more than 7 days (show assignee and days since last update)
   - Skip cards assigned to others with recent activity (< 7 days)

4. **Present options to the user**:
   - Show the top 5-7 available cards grouped by priority
   - For each card show: `#number - Title [Priority] [Module labels if any]`
   - If showing "Potentially Available" cards, clearly indicate: `(Assigned to @username, inactive for X days - check with them before picking up)`

5. **Use the AskUserQuestion tool** to let the user choose which card to pick up. Include an option to see more cards if needed.

6. **When the user selects a card**:
   - Assign the card to the current user using: `gh issue edit <number> --add-assignee @me`
   - Confirm the assignment was successful
   - Show the issue URL so they can start working on it

## Example Output Format

```
## Available Stories by Priority

### P0 - Critical
- #21 - S01.1: Customer Registration [module:customer]
- #22 - S01.2: Email Verification [module:customer]

### P1 - High
- #27 - Developer Onboarding & CONTRIBUTING.md

### Potentially Available (inactive > 7 days)
- #15 - Some other story [P1] (Assigned to @otherdev, inactive for 12 days - check with them first)

Which card would you like to pick up?
```

## Important Notes
- Always get the current GitHub username first with `gh api user --jq .login`
- Cards already assigned to the current user should be shown separately as "Your current assignments"
- Be helpful and suggest which card might be a good starting point based on priority and dependencies
