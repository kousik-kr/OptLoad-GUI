#!/bin/bash

# Quick setup script for VRPLU-OptLoad project
# This script prepares the environment and downloads necessary datasets

set -e

echo "================================================"
echo "  VRPLU-OptLoad Quick Setup"
echo "================================================"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Step 1: Check Java
echo "Step 1: Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    echo -e "${GREEN}✓${NC} Java is installed: $JAVA_VERSION"
else
    echo -e "${YELLOW}⚠${NC} Java is not installed"
    echo "  Please install JDK 11 or higher"
    exit 1
fi
echo ""

# Step 2: Check Python (for gdown)
echo "Step 2: Checking Python installation..."
if command -v python3 &> /dev/null; then
    PYTHON_VERSION=$(python3 --version 2>&1)
    echo -e "${GREEN}✓${NC} Python is installed: $PYTHON_VERSION"
    
    # Check for pip
    if command -v pip3 &> /dev/null; then
        echo -e "${GREEN}✓${NC} pip3 is available"
        
        # Check for gdown
        if python3 -c "import gdown" 2>/dev/null; then
            echo -e "${GREEN}✓${NC} gdown is installed"
        else
            echo -e "${YELLOW}⚠${NC} gdown is not installed"
            echo "  Installing gdown for dataset downloads..."
            pip3 install --user gdown || echo "  Failed to install gdown, manual download may be needed"
        fi
    fi
else
    echo -e "${YELLOW}⚠${NC} Python3 is not installed (optional for automatic downloads)"
fi
echo ""

# Step 3: Download datasets
echo "Step 3: Checking and downloading datasets..."
if [ -x "scripts/download-dataset.sh" ]; then
    ./scripts/download-dataset.sh
else
    echo -e "${YELLOW}⚠${NC} Download script not found"
fi
echo ""

# Step 4: Make scripts executable
echo "Step 4: Setting up scripts..."
chmod +x run.sh 2>/dev/null || true
chmod +x scripts/*.sh 2>/dev/null || true
echo -e "${GREEN}✓${NC} Scripts are executable"
echo ""

# Step 5: Summary
echo "================================================"
echo "  Setup Complete!"
echo "================================================"
echo ""
echo "To run the project:"
echo "  ./run.sh"
echo ""
echo "To build with Maven (if installed):"
echo "  ./scripts/build.sh"
echo ""
echo "To download datasets manually:"
echo "  ./scripts/download-dataset.sh"
echo ""
echo "For more information, see:"
echo "  - README_NEW.md"
echo "  - GETTING_STARTED.md"
echo "  - DATASET.md"
echo ""
