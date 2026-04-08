#!/usr/bin/env bash
# Scans content for potential secrets before it hits disk.
# Used as a PreToolUse hook on Edit/Write operations.
# Receives JSON via stdin from Claude Code.
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

# Patterns that suggest secrets
PATTERNS=(
  'AKIA[0-9A-Z]{16}'                    # AWS Access Key
  'password\s*=\s*["\x27][^"\x27]+'     # Hardcoded passwords
  'secret\s*=\s*["\x27][^"\x27]+'       # Hardcoded secrets
  'token\s*=\s*["\x27][^"\x27]+'        # Hardcoded tokens
  'api[_-]?key\s*=\s*["\x27][^"\x27]+'  # API keys
  'BEGIN\s+(RSA|DSA|EC|OPENSSH)\s+PRIVATE\s+KEY'  # Private keys
)

for pattern in "${PATTERNS[@]}"; do
  if echo "$CONTENT" | grep -qEi "$pattern" 2>/dev/null; then
    echo "BLOCKED: Potential secret detected in ${FILE_PATH:-unknown file}" >&2
    echo "Pattern matched: $pattern" >&2
    echo "Remove the secret and use environment variables or a secrets manager instead." >&2
    exit 2
  fi
done

exit 0
