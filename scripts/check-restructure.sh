#!/bin/bash

# Post-restructure checklist script
# This script helps verify that the restructuring is complete

echo "================================================"
echo "  VRPLU-OptLoad Restructuring Checklist"
echo "================================================"
echo ""

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

check_mark="${GREEN}✓${NC}"
cross_mark="${RED}✗${NC}"
warning_mark="${YELLOW}⚠${NC}"

# Function to check if directory exists
check_dir() {
    if [ -d "$1" ]; then
        echo -e "${check_mark} Directory exists: $1"
        return 0
    else
        echo -e "${cross_mark} Missing directory: $1"
        return 1
    fi
}

# Function to check if file exists
check_file() {
    if [ -f "$1" ]; then
        echo -e "${check_mark} File exists: $1"
        return 0
    else
        echo -e "${cross_mark} Missing file: $1"
        return 1
    fi
}

echo "1. Checking Directory Structure..."
echo "-----------------------------------"
check_dir "src/main/java/com/vrplu"
check_dir "src/main/java/com/vrplu/domain"
check_dir "src/main/java/com/vrplu/algorithm"
check_dir "src/main/java/com/vrplu/algorithm/exact"
check_dir "src/main/java/com/vrplu/algorithm/heuristic"
check_dir "src/main/java/com/vrplu/solver"
check_dir "src/main/java/com/vrplu/util"
check_dir "src/main/java/com/vrplu/io"
check_dir "src/test/java/com/vrplu"
check_dir "data/raw"
check_dir "config"
check_dir "docs"
check_dir "scripts"
echo ""

echo "2. Checking Configuration Files..."
echo "-----------------------------------"
check_file "pom.xml"
check_file ".gitignore"
check_file "config/application.properties"
check_file "config/logback.xml"
echo ""

echo "3. Checking Documentation..."
echo "-----------------------------------"
check_file "docs/ARCHITECTURE.md"
check_file "docs/API.md"
check_file "docs/CONTRIBUTING.md"
check_file "GETTING_STARTED.md"
check_file "RESTRUCTURE_SUMMARY.md"
check_file "README_NEW.md"
echo ""

echo "4. Checking Scripts..."
echo "-----------------------------------"
check_file "scripts/migrate-code.sh"
check_file "scripts/build.sh"
check_file "scripts/show-structure.sh"

if [ -x "scripts/migrate-code.sh" ]; then
    echo -e "${check_mark} migrate-code.sh is executable"
else
    echo -e "${warning_mark} migrate-code.sh is not executable (run: chmod +x scripts/*.sh)"
fi

if [ -x "scripts/build.sh" ]; then
    echo -e "${check_mark} build.sh is executable"
else
    echo -e "${warning_mark} build.sh is not executable (run: chmod +x scripts/*.sh)"
fi
echo ""

echo "5. Checking Maven Setup..."
echo "-----------------------------------"
if command -v mvn &> /dev/null; then
    echo -e "${check_mark} Maven is installed"
    mvn -v | head -1
else
    echo -e "${cross_mark} Maven is not installed"
    echo "   Install from: https://maven.apache.org/"
fi
echo ""

echo "6. Checking Java Setup..."
echo "-----------------------------------"
if command -v java &> /dev/null; then
    echo -e "${check_mark} Java is installed"
    java -version 2>&1 | head -1
else
    echo -e "${cross_mark} Java is not installed"
    echo "   Install JDK 11 or higher"
fi
echo ""

echo "7. Data Migration Status..."
echo "-----------------------------------"
if [ -d "data/raw" ] && [ "$(ls -A data/raw)" ]; then
    echo -e "${check_mark} Data files found in data/raw/"
    echo "   Files: $(ls data/raw | wc -l)"
else
    echo -e "${warning_mark} No data files in data/raw/"
    echo "   Run migration: ./scripts/migrate-code.sh"
fi
echo ""

echo "8. Source Code Status..."
echo "-----------------------------------"
if [ -d "src" ] && [ "$(ls -A src/*.java 2>/dev/null)" ]; then
    echo -e "${warning_mark} Old Java files still in src/ directory"
    echo "   Original count: $(ls src/*.java 2>/dev/null | wc -l)"
    echo "   These can be removed after successful migration"
else
    echo -e "${check_mark} No old Java files in src/ (or already migrated)"
fi

if [ -d "src/main/java/com/vrplu" ] && [ "$(find src/main/java/com/vrplu -name '*.java' 2>/dev/null)" ]; then
    echo -e "${check_mark} Java files found in new structure"
    echo "   New location count: $(find src/main/java/com/vrplu -name '*.java' 2>/dev/null | wc -l)"
else
    echo -e "${warning_mark} No Java files in new structure yet"
    echo "   Run: ./scripts/migrate-code.sh"
fi
echo ""

echo "================================================"
echo "  Next Steps"
echo "================================================"
echo ""
echo "If you haven't migrated your code yet:"
echo "  1. Run: ./scripts/migrate-code.sh"
echo "  2. Fix import statements in migrated files"
echo "  3. Run: mvn clean compile"
echo ""
echo "To build the project:"
echo "  ./scripts/build.sh"
echo ""
echo "To run tests:"
echo "  mvn test"
echo ""
echo "For detailed guidance, see:"
echo "  - GETTING_STARTED.md"
echo "  - RESTRUCTURE_SUMMARY.md"
echo "  - docs/ARCHITECTURE.md"
echo ""
echo "================================================"
