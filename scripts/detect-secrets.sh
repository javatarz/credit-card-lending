#!/usr/bin/env bash
# Scans staged/modified files for potential secrets before they hit disk.
# Used as a PreToolUse hook on Edit/Write operations.
# Exit 0 = OK, Exit 2 = block with feedback.

FILE_PATH="${TOOL_INPUT_FILE_PATH:-$1}"

if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# Skip non-text files
if file --mime-type "$FILE_PATH" 2>/dev/null | grep -qv "text/"; then
  exit 0
fi

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
  if grep -qEi "$pattern" "$FILE_PATH" 2>/dev/null; then
    echo "BLOCKED: Potential secret detected in $FILE_PATH" >&2
    echo "Pattern matched: $pattern" >&2
    echo "Remove the secret and use environment variables or a secrets manager instead." >&2
    exit 2
  fi
done

exit 0
