#!/bin/bash
# Usage: ./run.sh [path-to-ortools-jar] [path-to-ortools-native-dir] [other args...]
# Example: ./run.sh /opt/ortools/lib/java/ortools.jar /opt/ortools/lib/ myArg1

set -euo pipefail

# Current workspace root
ROOT_DIR=$(pwd)

# Check and download dataset if needed
echo "Checking dataset availability..."
if [ -x "$ROOT_DIR/scripts/download-dataset.sh" ]; then
    "$ROOT_DIR/scripts/download-dataset.sh" || {
        echo "Dataset check failed. Please ensure dataset files are available."
        exit 1
    }
else
    echo "Warning: download-dataset.sh not found, skipping dataset check"
fi

echo ""
echo "Starting compilation and execution..."
echo ""

cd src/

# Compile all Java files
javac *.java

# Run the main Java program
java VRPLoadingUnloadingMain "$ROOT_DIR" "$@"

exit 0

