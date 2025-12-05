# Credit Card Lending Platform

> A demonstration of **intelligent Engineering (iE)** practices using Claude Code, with a credit card issuer system as the learning vehicle.

## What is This?

This repository exists to **demonstrate and practice intelligent Engineering (iE)** - a framework for leveraging AI throughout the entire Software Development Lifecycle. Rather than replacing the SDLC, iE applies AI assistance across all phases to reduce administrative overhead and empower developers to focus on strategy and creativity.

The credit card lending platform serves as a realistic, non-trivial domain to practice these skills. It covers the complete credit card lifecycle: customer onboarding, credit applications, decisioning, account management, transaction processing, billing, and payments.

## intelligent Engineering (iE)

### What is iE?

iE uses AI (Claude Code) as a collaborative partner throughout development:

- **Research & Analysis**: Understanding requirements, exploring codebases
- **Design**: Discussing approaches, making architectural decisions
- **Build**: TDD with AI assistance, code generation
- **Test**: Writing tests, reviewing coverage
- **Documentation**: Keeping docs in sync with code

### Techniques Used Here

| Technique | Purpose |
|-----------|---------|
| **Context Documentation** (`docs/context/`) | Dense, factual docs optimized for LLM consumption |
| **CLAUDE.md** | Project-specific instructions Claude Code reads automatically |
| **Slash Commands** | `/pickup`, `/start-dev`, `/update-context` for workflow automation |
| **Story Template** | Every story includes "Context Docs to Update" section |
| **TDD Workflow** | Red-Green-Refactor cycle with AI assistance |

### The Workflow

```
/pickup          → Assign yourself a prioritized story
/start-dev       → Begin TDD development with design discussion
                   → Red-Green-Refactor cycles with commits
                   → Update context docs
                   → Create PR
```

For more details, see the [intelligent Engineering Wiki Guide](https://github.com/javatarz/credit-card-lending/wiki/intelligent-Engineering) (coming soon).

## Quick Start

### Prerequisites

Install [mise](https://mise.jdx.dev/) for tool version management:

```bash
# macOS
brew install mise

# Or see https://mise.jdx.dev/getting-started.html for other platforms
```

### Setup

```bash
# Clone the repository
git clone git@github.com:javatarz/credit-card-lending.git
cd credit-card-lending

# Install tools (Java 25, Gradle) via mise
mise trust
mise install

# Start PostgreSQL
docker-compose up -d

# Build and verify
./gradlew build

# Run the application
./gradlew bootRun
```

The application will be available at:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### Using Claude Code

```bash
# Install Claude Code (if not already installed)
# See: https://docs.anthropic.com/en/docs/claude-code

# Start Claude Code in the project directory
claude

# Pick up a story
/pickup

# Start TDD development
/start-dev
```

## Documentation

| What | Where | Audience |
|------|-------|----------|
| **Design & Architecture** | [GitHub Wiki](https://github.com/javatarz/credit-card-lending/wiki) | Humans |
| **Context Documentation** | [`docs/context/`](docs/context/) | Claude Code / LLMs |
| **Technical Decisions** | [`docs/adr/`](docs/adr/) | Both |
| **Epics & Stories** | [GitHub Issues](https://github.com/javatarz/credit-card-lending/issues) | Both |
| **Claude Code Instructions** | [`CLAUDE.md`](CLAUDE.md) | Claude Code |
| **API Documentation** | [Swagger UI](http://localhost:8080/swagger-ui.html) | Both |

### Key Context Documents

- [`docs/context/overview.md`](docs/context/overview.md) - System architecture
- [`docs/context/conventions.md`](docs/context/conventions.md) - Code patterns and standards
- [`docs/context/testing.md`](docs/context/testing.md) - TDD principles and test strategy
- [`docs/context/current-state.md`](docs/context/current-state.md) - What's built vs planned

## Tech Stack

| Component | Choice |
|-----------|--------|
| Language | Java 25 LTS |
| Framework | Spring Boot 4.x (Spring Framework 7) |
| Database | PostgreSQL 16.x |
| Build | Gradle 9.x (Kotlin DSL) |
| Migrations | Liquibase |
| Infrastructure | Terraform, AWS ECS Fargate |
| Testing | JUnit 5, AssertJ, Testcontainers, ArchUnit |

See [ADR-003](docs/adr/ADR-003-technology-stack.md) for detailed rationale.

## Architecture

**Modular Monolith** with clear module boundaries, designed for future service extraction.

```
credit-card-lending/
├── modules/           # Feature modules
│   ├── customer/      # Customer identity & profile
│   ├── application/   # Credit application processing
│   ├── decisioning/   # Credit decision engine
│   └── ...
├── shared/            # Shared libraries
│   ├── kernel/        # Domain primitives
│   └── events/        # Event definitions
└── platform/
    └── api-gateway/   # API entry point
```

See the [Architecture Wiki](https://github.com/javatarz/credit-card-lending/wiki/Architecture) for details.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for setup instructions and contribution guidelines.

**Quick contribution workflow:**
1. Run `/pickup` to assign yourself a story
2. Run `/start-dev` to begin TDD development
3. Follow the Red-Green-Refactor cycle
4. Update context docs as needed
5. Create a PR

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
