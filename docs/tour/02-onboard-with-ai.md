# Stop 2: Onboard With AI

## The Point of This Stop

You're reading documentation about a codebase you've never seen before. Normally, onboarding means:
- Reading outdated wikis
- Asking teammates who are busy
- Grep-ing through code hoping to find answers
- Trial and error

**With iE, onboarding is a conversation.**

If you're using Claude Code right now, you can ask questions about this codebase and get meaningful answers. Not generic responses - answers grounded in the actual code, documentation, and design decisions.

## Try It Now

If you're in Claude Code, try asking:

> "What is the Customer module responsible for?"

> "How does SSN encryption work in this system?"

> "What's the difference between a Customer and an Application?"

> "Where would I add a new validation rule for credit applications?"

Go ahead. Ask. The answers come from the context docs, wiki, and code itself.

## Why This Works

This isn't magic. It works because:

1. **Context documentation** (`docs/context/`) - Dense, factual docs written for LLM consumption
2. **CLAUDE.md** - Project-specific instructions Claude reads automatically
3. **GitHub Wiki** - Architecture and design docs accessible via the `/wiki` skill
4. **Consistent patterns** - Code follows conventions the AI can recognize

The next stops explain each of these in detail.

## The Shift

Traditional onboarding: *Read everything, hope you remember it, ask when stuck*

iE onboarding: *Ask questions as they arise, get grounded answers, explore as needed*

You don't need to front-load all the knowledge. You retrieve it when you need it.

## Without Claude Code

If you're reading this as markdown (without Claude Code), you can still learn from the structure. The context docs in `docs/context/` show how to write documentation that enables this kind of AI collaboration.

---

**Next:** [Stop 3: Context Docs](03-context-docs.md) - How to write docs that make this possible
