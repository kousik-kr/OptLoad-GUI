#!/bin/bash

# Build script for VRPLU-OptLoad project

echo "========================================="
echo "  VRPLU-OptLoad Build Script"
echo "========================================="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven from https://maven.apache.org/"
    exit 1
fi

# Clean previous builds
echo "Step 1: Cleaning previous builds..."
mvn clean
if [ $? -ne 0 ]; then
    echo "Clean failed!"
    exit 1
fi
echo "✓ Clean completed"
echo ""

# Compile the project
echo "Step 2: Compiling source code..."
mvn compile
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi
echo "✓ Compilation successful"
echo ""

# Run tests
echo "Step 3: Running tests..."
mvn test
if [ $? -ne 0 ]; then
    echo "Tests failed!"
    exit 1
fi
echo "✓ Tests passed"
echo ""

# Package the application
echo "Step 4: Packaging application..."
mvn package
if [ $? -ne 0 ]; then
    echo "Packaging failed!"
    exit 1
fi
echo "✓ Package created"
echo ""

echo "========================================="
echo "  Build completed successfully!"
echo "========================================="
echo ""
echo "Artifacts:"
echo "  - JAR file: target/vrplu-optload-1.0.0.jar"
echo "  - JAR with dependencies: target/vrplu-optload-1.0.0-jar-with-dependencies.jar"
echo ""
echo "To run the application:"
echo "  java -jar target/vrplu-optload-1.0.0-jar-with-dependencies.jar"
echo ""
