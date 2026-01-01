# Metrics & Measurement

## Why Read This?

Track intelligent Engineering (iE) effectiveness. Read when setting up metrics, reviewing sprint performance, or measuring AI-assisted development impact.

**Prerequisites:** [overview.md](overview.md)
**Related:** [conventions.md](conventions.md), [testing.md](testing.md)

---

## Purpose

"If you can't measure it, you can't improve it." This doc defines what to track and how to measure iE effectiveness in this repository.

## Metrics Philosophy

| Type | Purpose | Example |
|------|---------|---------|
| **Leading Indicators** | Early signals of effectiveness | Story completion rate |
| **Lagging Indicators** | Outcome measurements | Deployment frequency |
| **Quality Metrics** | Code health | Test coverage, code review findings |
| **Process Metrics** | Workflow efficiency | Commit size, review time |

## Metrics to Track

### 1. Small Batch Compliance

**What:** Percentage of commits that follow <200 line guideline

**Why:** Core iE principle - small batches enable thorough review

**How to measure:**
```bash
# Check commits in last 30 days
git log --since="30 days ago" --oneline --no-merges | \
  while read commit; do
    hash=$(echo "$commit" | awk '{print $1}')
    lines=$(git show $hash --stat | tail -1 | awk '{print $4 + $6}')
    echo "$hash,$lines"
  done | \
  awk -F',' '{if ($2 < 200) compliant++; total++} END {print "Compliance:", compliant/total*100"%"}'
```

**Target:** >80% of commits <200 lines

**Track in:** `.github/workflows/metrics.yml` (weekly summary)

### 2. DORA Metrics

#### Deployment Frequency

**What:** How often we push to main (trunk-based = deployments)

**Why:** Measures delivery velocity

**How to measure:**
```bash
# Commits to main per week
git log --since="7 days ago" --oneline --no-merges | wc -l
```

**Target:** >10 commits/week (daily deployments)

#### Lead Time for Changes

**What:** Time from story created → closed

**Why:** Measures workflow efficiency

**How to measure:**
```bash
# Average days from created to closed (last 10 stories)
gh issue list --state closed --label story --limit 10 --json number,createdAt,closedAt | \
  jq -r '.[] | [.number, .createdAt, .closedAt] | @csv' | \
  awk -F',' '{
    gsub(/"/, "", $2); gsub(/"/, "", $3);
    created = mktime(gensub(/[-T:]/, " ", "g", substr($2, 1, 19)));
    closed = mktime(gensub(/[-T:]/, " ", "g", substr($3, 1, 19)));
    days = (closed - created) / 86400;
    sum += days; count++;
  } END {print "Avg Lead Time:", sum/count, "days"}'
```

**Target:** <5 days for P0 stories

#### Change Failure Rate

**What:** Percentage of commits that cause issues

**Why:** Measures quality of AI-assisted code

**How to measure:**
```bash
# Count bug fixes vs total commits
bug_fixes=$(git log --since="30 days ago" --grep="Fix" --oneline --no-merges | wc -l)
total=$(git log --since="30 days ago" --oneline --no-merges | wc -l)
echo "scale=2; $bug_fixes / $total * 100" | bc
```

**Target:** <15% (Elite: <15%, High: 16-30%)

#### Time to Restore Service

**What:** Time from bug reported → fixed

**Why:** Measures recovery speed

**How to measure:**
```bash
# Average time to close bug issues
gh issue list --state closed --label bug --limit 10 --json number,createdAt,closedAt | \
  jq -r '.[] | [((.closedAt | fromdateiso8601) - (.createdAt | fromdateiso8601))/3600] | @csv' | \
  awk -F',' '{sum += $1; count++} END {print "Avg Time to Restore:", sum/count, "hours"}'
```

**Target:** <24 hours

### 3. AI-Specific Metrics

#### Stories Completed with AI Assistance

**What:** Percentage of stories where AI was primary collaborator

**Why:** Tracks AI adoption rate

**How to measure:**
```bash
# Count stories with "Generated with Claude Code" in commits
ai_stories=$(gh issue list --state closed --label story --limit 20 --json number | \
  jq -r '.[].number' | \
  while read num; do
    if git log --all --grep="#$num" --grep="Claude Code" --oneline | grep -q .; then
      echo "1"
    fi
  done | wc -l)

total_stories=20
echo "scale=2; $ai_stories / $total_stories * 100" | bc
```

**Target:** >80% (this is an iE demonstration project)

#### Context Doc Update Rate

**What:** Percentage of stories that updated context docs

**Why:** Measures "living documentation" practice

**How to measure:**
```bash
# Check if docs/context/ was modified in commits for each story
stories_with_docs=$(gh issue list --state closed --label story --limit 20 --json number | \
  jq -r '.[].number' | \
  while read num; do
    if git log --all --grep="#$num" --name-only | grep -q "docs/context/"; then
      echo "1"
    fi
  done | wc -l)

echo "scale=2; $stories_with_docs / 20 * 100" | bc
```

**Target:** >60% (not all stories change domain/architecture)

#### Average Commit Size

**What:** Average lines changed per commit

**Why:** Monitors small batch adherence

**How to measure:**
```bash
git log --since="30 days ago" --stat --oneline --no-merges | \
  grep "files changed" | \
  awk '{ins+=$4; del+=$6; count++} END {print "Avg commit size:", (ins+del)/count, "lines"}'
```

**Target:** <150 lines average

### 4. Code Quality Metrics

#### Test Coverage

**What:** Percentage of code covered by tests

**Why:** Quality indicator for AI-generated code

**How to measure:**
```bash
# Run Jacoco, parse report
./gradlew test jacocoTestReport
# Parse build/reports/jacoco/test/html/index.html
grep -oP 'Total[^%]+\K[0-9]+(?=%)' build/reports/jacoco/test/html/index.html | tail -1
```

**Target:** >80% line coverage

#### Code Review Findings

**What:** Blockers/Warnings/Suggestions from review skill

**Why:** Measures AI code quality, review effectiveness

**How to measure:**
- Track findings by category when `/review` is run
- Log to `metrics/reviews.csv`:
  ```csv
  date,story,blockers,warnings,suggestions
  2026-01-01,42,0,2,5
  ```

**Target:** <5% commits with blockers

#### Technical Debt

**What:** Count of TODOs, missing tests, long methods

**Why:** Accumulation indicator

**How to measure:**
```bash
# Count TODOs
grep -r "TODO" modules/ --include="*.java" | wc -l

# Count classes without tests
comm -23 \
  <(find modules/ -name "*.java" -not -name "*Test.java" | sort) \
  <(find modules/ -name "*Test.java" | sed 's/Test.java/.java/' | sort) | \
  wc -l
```

**Target:** Trending down month-over-month

### 5. Developer Experience Metrics

#### Developer Sentiment

**What:** Subjective rating of AI effectiveness

**Why:** Leading indicator of tool value

**How to measure:**
- Weekly retro question: "How effective was AI assistance this week? (1-5)"
- Track in `metrics/sentiment.csv`:
  ```csv
  week,rating,notes
  2026-W01,4,"Great for boilerplate, struggled with complex validation"
  ```

**Target:** Average >4/5

#### Time Saved (Estimated)

**What:** Developer estimate of time AI saved vs manual

**Why:** ROI indicator

**How to measure:**
- At story completion, ask: "How long would this have taken manually?"
- Compare to actual time
- Track in story comments or `metrics/time-saved.csv`

**Target:** >30% time savings on average

## Tracking Infrastructure

### Option 1: Manual Tracking (MVP)

Create `metrics/` directory (gitignored):
```
metrics/
├── weekly-summary.md    # Manual weekly summary
├── sentiment.csv        # Developer sentiment
├── time-saved.csv      # Time savings estimates
└── reviews.csv         # Code review findings
```

Update weekly during retros.

### Option 2: Automated (Future)

GitHub Actions workflow:
```yaml
name: Weekly Metrics

on:
  schedule:
    - cron: '0 9 * * 1'  # Monday 9am

jobs:
  metrics:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Calculate Metrics
        run: |
          bash scripts/calculate-metrics.sh
      - name: Create Issue
        run: |
          gh issue create --title "Weekly Metrics $(date +%Y-W%V)" \
            --body-file metrics-summary.md \
            --label metrics
```

### Option 3: Metrics Skill (Recommended)

Create `.claude/skills/metrics/SKILL.md` that:
- Runs calculations on demand
- Presents summary to user
- Logs to metrics/ directory

## Baseline Establishment

**Before scaling AI usage**, establish baseline:

1. **Pick 5 representative stories** (mix of complexity)
2. **Implement manually** (no AI assistance)
3. **Track:**
   - Time to complete
   - Lines of code
   - Test coverage
   - Bugs found in review
4. **Document in `metrics/baseline.md`**

Then compare future AI-assisted work against this baseline.

## Reporting

### Weekly Summary (During Retros)

Template:
```markdown
## Week YYYY-Wnn Metrics

### DORA Metrics
- Deployment Frequency: X commits to main
- Lead Time: X days average
- Change Failure Rate: X%
- Time to Restore: X hours average

### AI Effectiveness
- Stories with AI assistance: X%
- Context doc update rate: X%
- Small batch compliance: X%

### Quality
- Test coverage: X%
- Code review blockers: X commits
- Technical debt: X TODOs

### Developer Experience
- Sentiment: X/5
- Estimated time saved: X hours

### Trends
- [Metric] is ↑/↓/→ compared to last week
- Key insight: [observation]

### Actions
- [Action item 1]
- [Action item 2]
```

### Monthly Dashboard

Track trends:
- DORA metrics over time
- Small batch compliance
- AI adoption rate
- Test coverage

**Visualization:** Spreadsheet or simple script generating markdown tables

## When to Review Metrics

| Frequency | Focus | Actions |
|-----------|-------|---------|
| **Weekly** | Sprint performance, immediate issues | Adjust practices |
| **Monthly** | Trends, accumulation | Strategic adjustments |
| **Quarterly** | Long-term impact | Re-evaluate tools, approaches |

## Red Flags

| Metric | Red Flag | Action |
|--------|----------|--------|
| **Small batch compliance** | <60% | Reinforce in reviews, break stories smaller |
| **Change failure rate** | >30% | Review practices, add quality gates |
| **Context doc updates** | <40% | Emphasize in Definition of Done |
| **Test coverage** | <70% or declining | Stricter TDD enforcement |
| **Lead time** | >10 days | Identify bottlenecks, reduce scope |
| **Developer sentiment** | <3/5 | Retro on AI tool effectiveness |

## Success Indicators

You're succeeding with iE when:
- ✅ Small batches are habitual (>80% compliance)
- ✅ Deployment frequency increasing
- ✅ Change failure rate stable or decreasing
- ✅ Context docs stay current (>60% update rate)
- ✅ Developer sentiment positive (>4/5)
- ✅ Time savings visible (>30% on average)

## Next Steps

1. **Establish baseline** (5 manual stories)
2. **Set up tracking** (manual or automated)
3. **First measurement** (after 2 weeks of AI-assisted work)
4. **Weekly reviews** (during retros)
5. **Adjust practices** based on data

## Related Documentation

- [conventions.md](conventions.md) - Code conventions and git workflow
- [`CLAUDE.md`](../../CLAUDE.md) - Small batch principle
- [testing.md](testing.md) - Test coverage strategy
