# Stop 7: Try It Yourself

## You've Seen the Tour. Now Do It.

The best way to learn is by doing. Here are ways to practice:

## Option 1: Pick Up a Real Story

If you want to contribute:

```bash
/pickup
```

Look for stories labeled `good-first-issue` or documentation stories. These are designed to be approachable for newcomers.

## Option 2: Explore the Codebase

Stay in this repo and explore using AI:

```
"Walk me through how a customer gets created"

"What would I need to change to add a new customer status?"

"Explain the module boundary between Customer and Application"
```

The context docs make these questions answerable. Try asking something about your domain.

## Option 3: Apply to Your Own Project

Take what you've learned and apply it:

1. **Create a `docs/context/` structure** in your project
   - Start with `overview.md` and `conventions.md`
   - Add domain and module docs as needed

2. **Write a `CLAUDE.md`** with your project's rules
   - Commit message format
   - Code conventions
   - Where to find things

3. **Try the workflow** on a small feature
   - Write the failing test first
   - Let AI help implement
   - Small, focused commits

## What You've Learned

| Stop | Key Takeaway |
|------|--------------|
| 1. Welcome | This is a learning sandbox, not a demo |
| 2. Onboard With AI | Onboarding is a conversation, not a document dump |
| 3. Context Docs | LLM-optimized docs enable meaningful AI collaboration |
| 4. CLAUDE.md | Project instructions shape AI behavior |
| 5. Real Commit | TDD + AI = tests and implementation together |
| 6. Workflow | /pickup → /start-dev → commit → push |

## Questions?

- **GitHub Discussions**: Share your experience, ask questions
- **Issues**: Found a bug or have a suggestion? Open an issue
- **The Screencast**: Watch the full workflow in action

## The Meta Point

This tour itself was created using the workflow it describes. Story #53 in the backlog, picked up with `/pickup`, developed with TDD, committed in small batches.

That's the point. The system documents itself. The workflow builds the workflow.

---

**Tour complete.**

Go build something.
