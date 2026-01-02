# Stop 5: A Real Commit

## The Commit

Let's look at a real commit from this repository:

```
commit 0974aa0

Add EncryptionService with AES-256-GCM implementation #23

Created EncryptionService interface and AwsSecretsManagerEncryptionService
implementation using AES-256-GCM encryption. Each encryption generates a
new IV for security. Added AWS Secrets Manager SDK dependency.

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>
```

This commit was part of Story #23 (Customer Profile Completion), which required encrypting SSNs for PCI compliance.

## What's In This Commit

| File | Purpose |
|------|---------|
| `EncryptionService.java` | Interface defining encrypt/decrypt contract |
| `AwsSecretsManagerEncryptionService.java` | AES-256-GCM implementation |
| `EncryptionException.java` | Custom exception for encryption failures |
| `AwsSecretsManagerEncryptionServiceTest.java` | Tests proving it works |
| `build.gradle.kts` | Added AWS SDK dependency |

## The TDD Pattern

Notice the test file. This commit includes tests that verify:

1. **Encrypt and decrypt round-trip** - Data comes back unchanged
2. **Different ciphertext each time** - Same plaintext produces different encrypted values (because of random IV)
3. **Invalid input handling** - Garbage in throws proper exception

The tests were written *before* the implementation. That's TDD.

## Look At This

Run this to see the full commit:
```bash
git show 0974aa0
```

Notice:
- The commit message explains *what* and *why*, not *how*
- The `#23` links to the story
- The `Co-Authored-By` shows AI collaboration
- Tests and implementation are in the same commit

## The Story Sequence

This commit was one of several in Story #23:

```
27850e2 Add Address value object for customer profiles #23
93bf560 Add PROFILE_COMPLETE status to CustomerStatus enum #23
6e6b65c Add CustomerProfile entity with SSN encryption fields #23
7d82762 Add CustomerProfileRepository #23
0974aa0 Add EncryptionService with AES-256-GCM implementation #23  <-- You are here
b4ca7e2 Add profile DTOs and custom validators #23
561cc89 Implement completeProfile service method #23
f9d1f32 Add profile completion REST endpoint and exception handlers #23
...
```

Each commit is a small, focused change. Easy to review, easy to revert if needed.

## The AI Collaboration

The AI didn't just generate code. The conversation went something like:

1. **Discuss the requirement**: "We need to encrypt SSNs for PCI compliance"
2. **Design decision**: "AES-256-GCM with random IV per encryption"
3. **Write the test first**: Test for round-trip, uniqueness, error handling
4. **Implement to pass tests**: The actual encryption code
5. **Refactor**: Clean up, add comments, handle edge cases

The human stayed in control. The AI accelerated the work.

---

**Next:** [Stop 6: The Workflow](06-workflow.md) - The full cycle from story to shipped code
