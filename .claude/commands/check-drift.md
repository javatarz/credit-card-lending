# Check Documentation Drift

Detect misalignment between documentation and actual implementation.

## What is Drift?

**Documentation drift** occurs when docs describe code that doesn't exist, or claim features are "built" when they aren't.

**This is NOT about deployment drift** - this checks docs vs code in the repository.

## Instructions

### 1. Check for Missing Files Referenced in Docs

```bash
# Extract file paths from context docs
grep -r "src/main" docs/context/ | \
  sed -n 's/.*\(src\/main[^[:space:]]*\).*/\1/p' | \
  sort -u | \
  while read file; do
    if [ ! -f "$file" ]; then
      echo "❌ Doc references missing file: $file"
    fi
  done
```

### 2. Check for Orphaned Module Docs

```bash
# Check if module docs have corresponding code
for doc in docs/context/modules/*-module.md; do
  module_name=$(basename "$doc" -module.md)
  if [ ! -d "modules/$module_name" ]; then
    echo "❌ Module doc exists but no code: $module_name"
  fi
done
```

### 3. Check for Orphaned Domain Docs

```bash
# Check if domain docs have corresponding code references
for doc in docs/context/domain/*.md; do
  domain_name=$(basename "$doc" .md)
  # Search for the domain in actual code
  if ! grep -r "$domain_name" modules/ --include="*.java" >/dev/null 2>&1; then
    echo "⚠️  Domain doc exists but not referenced in code: $domain_name"
  fi
done
```

### 4. Verify "What's Built" in current-state.md

**Manual review required** - read `docs/context/current-state.md` and verify each item marked as "built":

1. Read the "What's Built" section
2. For each completed feature, verify:
   - Code exists in the module
   - Tests exist and pass
   - API endpoints work (if applicable)
3. Report any discrepancies

### 5. Check for Stale ADRs

```bash
# Check if ADR index matches actual files
echo "=== ADR Index Check ==="
echo "ADRs in index (docs/adr/README.md):"
grep "ADR-" docs/adr/README.md | grep -o "ADR-[0-9]*" | sort -u

echo ""
echo "ADRs in filesystem:"
ls docs/adr/ADR-*.md 2>/dev/null | sed 's/.*\(ADR-[0-9]*\).*/\1/' | sort -u

echo ""
echo "Run: diff <(grep 'ADR-' docs/adr/README.md | grep -o 'ADR-[0-9]*' | sort -u) <(ls docs/adr/ADR-*.md 2>/dev/null | sed 's/.*\(ADR-[0-9]*\).*/\1/' | sort -u)"
```

## Present Findings

Use AskUserQuestion to present findings:

```markdown
## Documentation Drift Detected

### Missing Files
- src/main/java/foo/Bar.java (referenced in domain/customer.md)

### Orphaned Docs
- Module doc for "billing" exists but no code yet

### current-state.md Issues
- Claims "Transaction posting" is built, but TransactionService doesn't exist

### ADR Index Issues
- ADR-007 exists but not in index

### Recommendations
1. Update current-state.md to mark transaction posting as "planned"
2. Remove billing module doc or create placeholder code
3. Add ADR-007 to index
```

Options:
- Fix now
- Create issue to fix later
- Ignore (document why)

## When to Run

- Weekly during active development
- Before milestone releases
- When onboarding new team members
- After completing epics

## Fixing Drift

| Drift Type | Fix |
|------------|-----|
| **Doc references missing code** | Remove reference OR implement code |
| **Code exists, not documented** | Update docs OR mark as internal |
| **current-state.md claims built but isn't** | Mark as "planned" OR implement |
| **Orphaned docs** | Delete OR add placeholder code |
| **ADR index mismatch** | Update index OR rename file |

## Prevention

- Update docs atomically with code (Definition of Done)
- Run `/update-context` after completing stories
- Review `current-state.md` during sprint planning
- Link docs to code with comments (sparingly)

## Important Notes

- **This checks doc-code drift**, not deployment drift
- Requires human judgment for "What's Built" verification
- Some "drift" is expected during active development
- Clean up drift before marking epics complete
