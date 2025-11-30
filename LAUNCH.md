# Quick Launch Guide

## Single Command Execution

### Option 1: Batch File (Easiest)
```cmd
run.bat
```
Just double-click `run.bat` or run it from command prompt.

### Option 2: PowerShell Script
```powershell
powershell -ExecutionPolicy Bypass -File setup-and-run.ps1
```

### Option 3: Direct PowerShell (if execution policy allows)
```powershell
.\setup-and-run.ps1
```

## Script Features

The `setup-and-run.ps1` script automatically:
1. ✅ Checks Java 21+ installation
2. ✅ Builds classpath from Maven repository
3. ✅ Compiles Java sources (if needed)
4. ✅ Copies resource files
5. ✅ Launches the JavaFX GUI

## Advanced Options

### Clean build and run
```powershell
.\setup-and-run.ps1 -Clean
```

### Skip compilation (if already compiled)
```powershell
.\setup-and-run.ps1 -SkipCompile
```

## Requirements

- Java 21 or later installed and in PATH
- Dependencies in Maven local repository (`~/.m2/repository`)
  - If missing, run: `mvn dependency:resolve`

## Troubleshooting

**"Java not found"**
- Ensure Java 21+ is installed and `java` command is in PATH

**"Missing JavaFX dependencies"**
- Run: `mvn dependency:resolve` to download dependencies

**"Compilation failed"**
- Check for syntax errors in Java source files
- Verify Java version compatibility

## Files

- `run.bat` - Quick launcher (double-click to run)
- `setup-and-run.ps1` - Main setup and launch script
- `launch.ps1` - Simple launch script (no setup checks)
