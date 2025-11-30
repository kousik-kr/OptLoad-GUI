#!/bin/bash
# OptLoad GUI - Automated Setup and Launch Script for Linux/Ubuntu
# Single command to compile and run the JavaFX application

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo ""
echo -e "${CYAN}╔══════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║       OptLoad - VRP Optimization GUI         ║${NC}"
echo -e "${CYAN}║              Java 21 Edition                 ║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════════╝${NC}"
echo ""

# [1/4] Check Java
echo -e "${YELLOW}[1/4] Checking Java...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}  ✗ Java not found${NC}"
    echo -e "${YELLOW}  Install Java 21: sudo apt install openjdk-21-jdk${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo -e "${GREEN}  ✓ $JAVA_VERSION${NC}"

# [2/4] Building classpath
echo -e "${YELLOW}[2/4] Building classpath...${NC}"

M2_REPO="$HOME/.m2/repository"
JFX_VERSION="11.0.2"
OS="linux"

# Build module path for JavaFX
MP="$M2_REPO/org/openjfx/javafx-base/$JFX_VERSION/javafx-base-$JFX_VERSION-$OS.jar"
MP="$MP:$M2_REPO/org/openjfx/javafx-controls/$JFX_VERSION/javafx-controls-$JFX_VERSION-$OS.jar"
MP="$MP:$M2_REPO/org/openjfx/javafx-graphics/$JFX_VERSION/javafx-graphics-$JFX_VERSION-$OS.jar"
MP="$MP:$M2_REPO/org/openjfx/javafx-fxml/$JFX_VERSION/javafx-fxml-$JFX_VERSION-$OS.jar"
MP="$MP:$M2_REPO/org/openjfx/javafx-swing/$JFX_VERSION/javafx-swing-$JFX_VERSION-$OS.jar"

# Build classpath
CP="target/classes"
CP="$CP:$M2_REPO/org/openjfx/javafx-base/$JFX_VERSION/javafx-base-$JFX_VERSION.jar"
CP="$CP:$M2_REPO/org/openjfx/javafx-controls/$JFX_VERSION/javafx-controls-$JFX_VERSION.jar"
CP="$CP:$M2_REPO/org/openjfx/javafx-graphics/$JFX_VERSION/javafx-graphics-$JFX_VERSION.jar"
CP="$CP:$M2_REPO/org/openjfx/javafx-fxml/$JFX_VERSION/javafx-fxml-$JFX_VERSION.jar"
CP="$CP:$M2_REPO/org/openjfx/javafx-swing/$JFX_VERSION/javafx-swing-$JFX_VERSION.jar"
CP="$CP:$M2_REPO/org/controlsfx/controlsfx/11.1.2/controlsfx-11.1.2.jar"
CP="$CP:$M2_REPO/io/github/typhon0/AnimateFX/1.2.3/AnimateFX-1.2.3.jar"
CP="$CP:$M2_REPO/de/jensd/fontawesomefx-fontawesome/4.7.0-9.1.2/fontawesomefx-fontawesome-4.7.0-9.1.2.jar"
CP="$CP:$M2_REPO/de/jensd/fontawesomefx-commons/9.1.2/fontawesomefx-commons-9.1.2.jar"
CP="$CP:$M2_REPO/org/slf4j/slf4j-api/2.0.7/slf4j-api-2.0.7.jar"
CP="$CP:$M2_REPO/ch/qos/logback/logback-classic/1.4.8/logback-classic-1.4.8.jar"
CP="$CP:$M2_REPO/ch/qos/logback/logback-core/1.4.8/logback-core-1.4.8.jar"
CP="$CP:$M2_REPO/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"
CP="$CP:$M2_REPO/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar"
CP="$CP:$M2_REPO/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar"

# Check if JavaFX dependencies exist
if [ ! -f "$M2_REPO/org/openjfx/javafx-base/$JFX_VERSION/javafx-base-$JFX_VERSION-$OS.jar" ]; then
    echo -e "${RED}  ✗ Missing JavaFX dependencies${NC}"
    echo -e "${YELLOW}  Run: mvn dependency:resolve${NC}"
    exit 1
fi

echo -e "${GREEN}  ✓ Classpath ready${NC}"

# [3/4] Check compilation
echo -e "${YELLOW}[3/4] Checking compilation...${NC}"

MAIN_CLASS="target/classes/gui/OptLoadGUI.class"
if [ ! -f "$MAIN_CLASS" ]; then
    echo -e "${YELLOW}  Compiling sources...${NC}"
    mkdir -p target/classes
    
    # Find all Java files
    JAVA_FILES=$(find src/main/java -name "*.java")
    
    # Compile
    javac -d target/classes -cp "$CP" -sourcepath src/main/java $JAVA_FILES
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}  ✗ Compilation failed${NC}"
        exit 1
    fi
    echo -e "${GREEN}  ✓ Compiled successfully${NC}"
else
    echo -e "${GREEN}  ✓ Already compiled${NC}"
fi

# [4/4] Launch application
echo -e "${YELLOW}[4/4] Launching application...${NC}"
echo ""
echo -e "${CYAN}════════════════════════════════════════════════${NC}"
echo ""

# Set DISPLAY if not set (for WSL/remote systems)
if [ -z "$DISPLAY" ]; then
    export DISPLAY=:0
fi

# Launch with JavaFX
java --module-path "$MP" \
     --add-modules javafx.controls,javafx.fxml,javafx.swing \
     -cp "$CP" \
     gui.OptLoadGUI

EXIT_CODE=$?

echo ""
echo -e "${CYAN}════════════════════════════════════════════════${NC}"

if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo -e "${GREEN}Application closed successfully${NC}"
else
    echo ""
    echo -e "${YELLOW}Application exited with code: $EXIT_CODE${NC}"
fi

exit $EXIT_CODE

