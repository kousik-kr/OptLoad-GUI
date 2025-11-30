# Quick Launch Guide

## Single Command Execution

### Windows

#### Option 1: Batch File (Easiest)
```cmd
run.bat
```
Just double-click `run.bat` or run it from command prompt.

#### Option 2: PowerShell Script
```powershell
powershell -ExecutionPolicy Bypass -File setup-and-run.ps1
```

#### Option 3: Direct PowerShell (if execution policy allows)
```powershell
.\setup-and-run.ps1
```

### Linux/Ubuntu

#### Single Command (Recommended)
```bash
chmod +x run.sh
./run.sh
```

The script automatically:
- ✅ Checks Java 21+ installation
- ✅ Builds classpath from Maven repository
- ✅ Compiles sources if needed
- ✅ Launches the GUI with proper JavaFX configuration

## Script Features

### Windows (setup-and-run.ps1)
The `setup-and-run.ps1` script automatically:
1. ✅ Checks Java 21+ installation
2. ✅ Builds classpath from Maven repository
3. ✅ Compiles Java sources (if needed)
4. ✅ Copies resource files
5. ✅ Launches the JavaFX GUI

### Linux/Ubuntu (run.sh)
The `run.sh` script automatically:
1. ✅ Checks Java 21+ installation
2. ✅ Builds classpath from Maven repository  
3. ✅ Compiles Java sources (if needed)
4. ✅ Sets DISPLAY for WSL/remote systems
5. ✅ Launches the JavaFX GUI

## Advanced Options

### Windows

#### Clean build and run
```powershell
.\setup-and-run.ps1 -Clean
```

### Skip compilation (if already compiled)
```powershell
.\setup-and-run.ps1 -SkipCompile
```

## Requirements

- Java 21 or later installed and in PATH
  - **Windows**: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use [Microsoft OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download)
  - **Ubuntu**: `sudo apt install openjdk-21-jdk`
- Dependencies in Maven local repository (`~/.m2/repository`)
  - If missing, run: `mvn dependency:resolve`

## Troubleshooting

**"Java not found"**
- Windows: Ensure Java 21+ is installed and `java` command is in PATH
- Ubuntu: Install with `sudo apt install openjdk-21-jdk`

**"Missing JavaFX dependencies"**
- Run: `mvn dependency:resolve` to download dependencies

**"Compilation failed"**
- Check for syntax errors in Java source files
- Verify Java version compatibility

**Ubuntu: "Permission denied"**
- Make script executable: `chmod +x run.sh`

**Ubuntu/WSL: Display issues**
- Install X server (VcXsrv or Xming for Windows)
- Set DISPLAY variable: `export DISPLAY=:0`

## Files

- **Windows:**
  - `run.bat` - Quick launcher (double-click to run)
  - `setup-and-run.ps1` - Main setup and launch script
  - `launch.ps1` - Simple launch script (no setup checks)
  
- **Linux/Ubuntu:**
  - `run.sh` - Automated setup and launch script
  
- **Documentation:**
  - `LAUNCH.md` - This file
