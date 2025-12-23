# Contributing to Credit Card Lending Platform

This guide will help you set up your development environment and understand our workflow.

## Prerequisites

### mise

We use [mise](https://mise.jdx.dev/) to manage tool versions (Java, etc.).

**Install mise:**
```bash
curl https://mise.run | sh
```

Or see the [mise installation guide](https://mise.jdx.dev/getting-started.html) for other options.

### Docker

You need a Docker engine to run PostgreSQL locally. Any of these will work:

| Engine | Install |
|--------|---------|
| **Docker Desktop** | [Download](https://www.docker.com/products/docker-desktop/) |
| **Colima** (macOS) | `brew install colima && colima start` |
| **OrbStack** (macOS) | [Download](https://orbstack.dev/) |
| **Rancher Desktop** | [Download](https://rancherdesktop.io/) |

### pre-commit (optional but recommended)

We use [pre-commit](https://pre-commit.com/) to validate documentation links before commits.

**Install pre-commit:**
```bash
# macOS
brew install pre-commit

# or via pip
pip install pre-commit
```

**Enable hooks:**
```bash
pre-commit install
```

This validates markdown links on every commit. To run manually:
```bash
pre-commit run --all-files
```

## Quick Start

```bash
# Clone the repository
git clone git@github.com:javatarz/credit-card-lending.git
cd credit-card-lending

# Run the setup script
./scripts/setup.sh
```

The setup script will:
1. Install Java 25 via mise
2. Start PostgreSQL via Docker
3. Build the project

### Optional: Wiki Clone

For local access to wiki documentation (auto-cloned by Claude Code's wiki skill):

```bash
# Derive wiki URL from your repo's origin
REPO_URL=$(git remote get-url origin)
git clone "${REPO_URL%.git}.wiki.git" docs/wiki
```

The `docs/wiki/` directory is gitignored - it's a separate repository.

## Project Structure

```
credit-card-lending/
├── modules/                    # Feature modules
│   └── customer/               # Customer identity & profile
├── shared/                     # Shared libraries
│   ├── kernel/                 # Domain primitives
│   ├── infrastructure/         # Cross-cutting concerns
│   └── events/                 # Event definitions
├── platform/
│   └── api-gateway/            # Spring Boot entry point
├── docs/
│   ├── adr/                    # Architecture Decision Records
│   └── context/                # Context documentation for iE
└── scripts/
    └── setup.sh                # Development setup script
```

## Creating a New Module

```bash
./gradlew createModule -PmoduleName=<name>
```

This scaffolds a new module in `modules/<name>/` with the standard package structure.

## Code Style

Follow the conventions in [`docs/context/conventions.md`](docs/context/conventions.md):

- **Package structure**: `api/` (public), `internal/` (private), `web/` (REST)
- **Naming**: `CustomerService`, `CustomerRepository`, `CustomerRegisteredEvent`
- **Testing**: `should_<expected>_when_<condition>` or `shouldX_whenY`

## Testing

We follow **Test-Driven Development (TDD)**. See [`docs/context/testing.md`](docs/context/testing.md) for principles.

**Run tests:**
```bash
./gradlew test
```

**TDD workflow:**
1. Write a failing test (RED)
2. Write minimum code to pass (GREEN)
3. Refactor while staying green (REFACTOR)
4. Commit

## Git Workflow

We use **trunk-based development** - all work happens directly on `main`:

| Practice | Description |
|----------|-------------|
| No feature branches | Commit directly to `main` |
| No pull requests | Push directly after local verification |
| Small commits | Each Red-Green-Refactor cycle = one commit |
| Always green | Never push failing tests |

**Before pushing:**
```bash
./gradlew build   # Must pass
git push origin main
```

See [`docs/context/conventions.md`](docs/context/conventions.md#git-workflow) for details.

## Development with Claude Code

This project uses [intelligent Engineering (iE)](https://github.com/javatarz/credit-card-lending/wiki/intelligent-Engineering) practices with Claude Code.

**Workflow:**
```bash
# Start Claude Code
claude

# Pick up a story
/pickup

# Start TDD development
/start-dev
```

## Useful Commands

| Command | Description |
|---------|-------------|
| `./scripts/setup.sh` | One-time development setup |
| `./gradlew build` | Build all modules |
| `./gradlew test` | Run all tests |
| `./gradlew bootRun` | Start the application (auto-starts PostgreSQL) |
| `./gradlew createModule -PmoduleName=X` | Scaffold a new module |
| `./gradlew composeUp` | Start PostgreSQL |
| `./gradlew composeDown` | Stop PostgreSQL |
| `docker-compose logs -f` | View PostgreSQL logs |

## Application URLs

When running locally:

| URL | Description |
|-----|-------------|
| http://localhost:8080 | API |
| http://localhost:8080/swagger-ui.html | Swagger UI |
| http://localhost:8080/actuator/health | Health check |

## Documentation

| Type | Location |
|------|----------|
| Architecture & Design | [GitHub Wiki](https://github.com/javatarz/credit-card-lending/wiki) |
| Technical Decisions | [`docs/adr/`](docs/adr/) |
| Code Conventions | [`docs/context/conventions.md`](docs/context/conventions.md) |
| Testing Strategy | [`docs/context/testing.md`](docs/context/testing.md) |
| Current State | [`docs/context/current-state.md`](docs/context/current-state.md) |

## Getting Help

- Check the [Wiki](https://github.com/javatarz/credit-card-lending/wiki) for architecture and design docs
- Read the context docs in `docs/context/` for detailed guidelines
- Open an [issue](https://github.com/javatarz/credit-card-lending/issues) for bugs or questions
