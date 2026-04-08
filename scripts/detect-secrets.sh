#!/usr/bin/env bash
# Scans content for potential secrets before it hits disk.
# Used as a PreToolUse hook on Edit/Write operations.
# Receives JSON via stdin from Claude Code.
# Uses gitleaks (https://github.com/gitleaks/gitleaks) for detection.
# Exit 0 = OK, Exit 2 = block with feedback.

INPUT=$(cat)

TOOL_NAME=$(echo "$INPUT" | jq -r '.tool_name')

# Extract the content to scan based on tool type
if [ "$TOOL_NAME" = "Write" ]; then
  CONTENT=$(echo "$INPUT" | jq -r '.tool_input.content // empty')
elif [ "$TOOL_NAME" = "Edit" ]; then
  CONTENT=$(echo "$INPUT" | jq -r '.tool_input.new_string // empty')
else
  exit 0
fi

if [ -z "$CONTENT" ]; then
  exit 0
fi

FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

# Scan content with gitleaks via stdin
RESULT=$(echo "$CONTENT" | gitleaks stdin --no-banner --exit-code 2 --report-format json --report-path - 2>/dev/null)
EXIT_CODE=$?

if [ "$EXIT_CODE" -eq 2 ]; then
  echo "BLOCKED: Secret detected in ${FILE_PATH:-unknown file}" >&2
  echo "$RESULT" | jq -r '.[] | "  Rule: \(.RuleID) — \(.Description)"' 2>/dev/null >&2
  echo "Remove the secret and use environment variables or a secrets manager instead." >&2
  exit 2
fi

exit 0
