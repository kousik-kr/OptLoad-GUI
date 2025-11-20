#!/bin/bash
# Usage: ./run.sh [path-to-ortools-jar] [path-to-ortools-native-dir] [other args...]
# Example: ./run.sh /opt/ortools/lib/java/ortools.jar /opt/ortools/lib/ myArg1

set -euo pipefail

# Current workspace root
ROOT_DIR=$(pwd)

cd src/

# Compile all Java files
javac *.java

# Run the main Java program
java VRPLoadingUnloadingMain "$ROOT_DIR" "$@"

exit 0

