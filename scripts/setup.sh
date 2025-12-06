#!/usr/bin/env bash
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "Setting up Credit Card Lending Platform development environment..."
echo ""

# 1. Check mise is installed
if ! command -v mise &> /dev/null; then
    echo -e "${RED}mise is not installed.${NC}"
    echo ""
    echo "Please install mise first:"
    echo "  curl https://mise.run | sh"
    echo ""
    echo "Or see: https://mise.jdx.dev/getting-started.html"
    exit 1
fi
echo -e "${GREEN}✓${NC} mise is installed"

# 2. Trust and install tools via mise
echo "Installing tools via mise..."
mise trust --yes
mise install
echo -e "${GREEN}✓${NC} Java installed via mise"

# 3. Check Docker CLI is available
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker CLI is not installed.${NC}"
    echo ""
    echo "Please install Docker. Options include:"
    echo "  - Docker Desktop: https://www.docker.com/products/docker-desktop/"
    echo "  - Colima (macOS): brew install colima && colima start"
    echo "  - OrbStack (macOS): https://orbstack.dev/"
    echo "  - Rancher Desktop: https://rancherdesktop.io/"
    exit 1
fi
echo -e "${GREEN}✓${NC} Docker CLI is installed"

# 4. Check Docker daemon is running
if ! docker info &> /dev/null; then
    echo -e "${RED}Docker daemon is not running.${NC}"
    echo ""
    echo "Please start your Docker engine:"
    echo "  - Docker Desktop: Open the Docker Desktop app"
    echo "  - Colima: colima start"
    echo "  - OrbStack: Open the OrbStack app"
    echo "  - Rancher Desktop: Open the Rancher Desktop app"
    exit 1
fi
echo -e "${GREEN}✓${NC} Docker daemon is running"

# 5. Start PostgreSQL via docker-compose
echo "Starting PostgreSQL..."
docker-compose up -d
echo -e "${GREEN}✓${NC} PostgreSQL is running"

# 6. Build the project
echo "Building the project (this may take a few minutes on first run)..."
./gradlew build
echo -e "${GREEN}✓${NC} Build successful"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Setup complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "Next steps:"
echo "  ./gradlew bootRun    # Start the application"
echo "  ./gradlew test       # Run tests"
echo ""
echo "The application will be available at:"
echo "  API:        http://localhost:8080"
echo "  Swagger UI: http://localhost:8080/swagger-ui.html"
echo "  Health:     http://localhost:8080/actuator/health"
echo ""
echo "See CONTRIBUTING.md for more commands and guidelines."
