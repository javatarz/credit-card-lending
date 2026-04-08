#!/usr/bin/env bash
# Auto-formats Java files after Write/Edit operations.
# Uses Spotless (the project's formatter).
# This is a PostToolUse hook, so it runs after every file change.

FILE_PATH="${TOOL_INPUT_FILE_PATH:-$1}"

if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# Only format Java files
if [[ "$FILE_PATH" != *.java ]]; then
  exit 0
fi

REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"

# Run Spotless on the specific file if possible, or on all files
# Spotless doesn't support single-file formatting easily, so we run it on all
# This is fast enough for a modular monolith
./gradlew spotlessApply --quiet 2>/dev/null || true

exit 0
