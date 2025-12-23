---
name: review
description: Review code for quality, style, and potential issues. Use when code has been written or modified, when user asks for code review, or when TDD autonomous mode needs quality assessment. Supports configurable strictness levels.
---

# Code Review Skill

Assess code quality and provide structured feedback.

## When This Skill Activates

- After code has been written or modified
- User explicitly asks for code review
- TDD skill invokes review in autonomous mode
- User says "review this", "check this code", "code review"

## Review Scope

| Scope | When | What to Review |
|-------|------|----------------|
| **Changed files** (default) | Normal reviews | Files modified in current cycle |
| **Related files** | User says "review with context" | Changed files + their imports/dependents |
| **Specific files** | User names files | Only the specified files |

## Threshold Levels

User can specify threshold:
- `review strict` - report everything
- `review normal` - report significant issues (default)
- `review relaxed` - report blockers only

### What Each Threshold Reports

| Category | Strict | Normal | Relaxed |
|----------|--------|--------|---------|
| **Blockers** | Yes | Yes | Yes |
| **Warnings** | Yes | Yes | No |
| **Suggestions** | Yes | No | No |

## Finding Categories

### Blockers (Must Fix)

Issues that prevent the code from working correctly or safely:

- Failing tests
- Compilation errors
- Security vulnerabilities (injection, XSS, secrets in code)
- Null pointer risks without handling
- Resource leaks (unclosed connections, streams)
- Race conditions
- Breaking API contracts

### Warnings (Should Fix)

Issues that work but violate good practices:

- Code duplication (DRY violation)
- Poor naming (unclear intent)
- Long methods (> 20 lines)
- Too many parameters (> 3)
- Missing error handling
- Violations of SOLID principles
- Test coverage gaps
- Magic numbers/strings

### Suggestions (Nice to Have)

Minor improvements:

- Style inconsistencies
- Slightly better naming options
- Minor simplifications
- Documentation opportunities
- Performance micro-optimizations

## Review Process

1. **Identify scope** - what files to review
2. **Read the code** - understand intent and implementation
3. **Check against standards** - use `docs/context/conventions.md`
4. **Categorize findings** - blockers, warnings, suggestions
5. **Present structured output**

## Output Format

Present findings in this structure:

```markdown
## Code Review: [threshold] mode

### Scope
- file1.java (modified)
- file2.java (modified)

### Blockers (X found)
1. **[File:Line]** Description of blocker
   - Why it's a problem
   - Suggested fix

### Warnings (X found)
1. **[File:Line]** Description of warning
   - Why it matters
   - Suggested improvement

### Suggestions (X found)
1. **[File:Line]** Description of suggestion

### Summary
- Blockers: X
- Warnings: X
- Suggestions: X
- **Verdict**: [PASS / NEEDS ATTENTION / BLOCKED]
```

### Verdict Logic

| Verdict | Condition |
|---------|-----------|
| **PASS** | No blockers, no warnings |
| **NEEDS ATTENTION** | No blockers, has warnings |
| **BLOCKED** | Has blockers |

## Standards Reference

When reviewing, check against:

### From `conventions.md`
- Package structure (`api/`, `internal/`, `web/`)
- Naming conventions (classes, methods, tests)
- Self-documenting code (no unnecessary comments)
- Clean code principles (small functions, single responsibility)

### From `testing.md`
- Test structure (Arrange-Act-Assert)
- Test naming (`should<Expected>_when<Condition>`)
- Single assertion per test (one logical concept)
- No testing implementation details

### Security Checklist
- No secrets in code
- Input validation at boundaries
- Parameterized queries (no SQL injection)
- Output encoding (no XSS)
- Proper authentication/authorization checks

## Integration with TDD Skill

When invoked by TDD autonomous mode:

1. Review skill receives the threshold level
2. Performs review on changed files
3. Returns findings
4. TDD skill decides to interrupt or continue based on threshold rules:
   - Strict: interrupt on any finding
   - Normal: interrupt on blockers or warnings
   - Relaxed: interrupt on blockers only

## Standalone Usage

User can invoke directly:

| Command | Behavior |
|---------|----------|
| "review this code" | Normal threshold, changed files |
| "review strict" | Strict threshold, changed files |
| "review with context" | Normal threshold, related files |
| "review src/main/java/..." | Normal threshold, specific file |

## Key Principles

- **Be helpful, not pedantic** - focus on meaningful improvements
- **Explain why** - don't just flag, explain the impact
- **Suggest fixes** - provide actionable guidance
- **Respect context** - consider the codebase style and constraints
- **Prioritize** - blockers first, then warnings, then suggestions
