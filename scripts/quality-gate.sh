#!/usr/bin/env bash
# Runs the full quality gate pyramid on TaskCompleted.
# This runs once when an agent says "done".
# Exit 0 = pass, Exit 2 = block with feedback.

set -e

REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"

echo "Running quality gates..." >&2

# Gate 1: Linting / static analysis (Spotless check)
echo "  [1/4] Checking code formatting..." >&2
if ! ./gradlew spotlessCheck --quiet 2>&1; then
  echo "BLOCKED: Code formatting issues found." >&2
  echo "Run './gradlew spotlessApply' to fix formatting, then try again." >&2
  exit 2
fi

# Gate 2: Compilation / type checking
echo "  [2/4] Compiling..." >&2
if ! ./gradlew compileJava compileTestJava --quiet 2>&1; then
  echo "BLOCKED: Compilation failed. Fix the errors above." >&2
  exit 2
fi

# Gate 3: Tests
echo "  [3/4] Running tests..." >&2
if ! ./gradlew test --quiet 2>&1; then
  echo "BLOCKED: Tests failed. Fix the failing tests above." >&2
  exit 2
fi

# Gate 4: Diff size check
echo "  [4/4] Checking diff size..." >&2
DIFF_LINES=$(git diff main --stat | tail -1 | grep -oE '[0-9]+ insertion' | grep -oE '[0-9]+' || echo "0")
DIFF_DELETIONS=$(git diff main --stat | tail -1 | grep -oE '[0-9]+ deletion' | grep -oE '[0-9]+' || echo "0")
TOTAL_CHANGES=$((${DIFF_LINES:-0} + ${DIFF_DELETIONS:-0}))

if [ "$TOTAL_CHANGES" -gt 500 ]; then
  echo "WARNING: Large diff ($TOTAL_CHANGES lines changed). Consider splitting into smaller commits." >&2
  echo "This is a warning, not a blocker. The human reviewer will assess." >&2
fi

echo "All quality gates passed." >&2
exit 0
