#!/bin/bash

# OptLoad GUI Launcher Script
# Launches the world-class VRP optimization GUI

echo "╔══════════════════════════════════════════════╗"
echo "║                                              ║"
echo "║         OptLoad - VRP Optimization GUI       ║"
echo "║                                              ║"
echo "╚══════════════════════════════════════════════╝"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Error: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6+ and try again"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "❌ Error: Java 11 or higher is required"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

echo "✓ Java version: $(java -version 2>&1 | head -n 1)"
echo "✓ Maven found: $(mvn -version | head -n 1)"
echo ""

# Parse command line arguments
CLEAN_BUILD=false
SKIP_TESTS=true

while [[ $# -gt 0 ]]; do
    case $1 in
        --clean)
            CLEAN_BUILD=true
            shift
            ;;
        --test)
            SKIP_TESTS=false
            shift
            ;;
        --help)
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  --clean    Clean build before running"
            echo "  --test     Run tests before launching"
            echo "  --help     Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Build if needed
if [ "$CLEAN_BUILD" = true ]; then
    echo "🔨 Building OptLoad GUI..."
    if [ "$SKIP_TESTS" = true ]; then
        mvn clean compile -DskipTests
    else
        mvn clean compile
    fi
    
    if [ $? -ne 0 ]; then
        echo "❌ Build failed"
        exit 1
    fi
    echo "✓ Build successful"
    echo ""
fi

# Launch the GUI
echo "🚀 Launching OptLoad GUI..."
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

mvn javafx:run

# Check exit code
if [ $? -eq 0 ]; then
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "✓ OptLoad GUI closed successfully"
else
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "❌ OptLoad GUI exited with errors"
    exit 1
fi
