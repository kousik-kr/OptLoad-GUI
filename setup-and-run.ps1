# Simple PowerShell launcher for OptLoad GUI

Write-Host "OptLoad GUI - Java 21 Edition" -ForegroundColor Cyan
Write-Host ""

# Set environment
$m2 = Join-Path $env:USERPROFILE ".m2\repository"
$jfx = "11.0.2"

# Build classpath
$cp = "target\classes"
$cp += ";$m2\org\controlsfx\controlsfx\11.1.2\controlsfx-11.1.2.jar"
$cp += ";$m2\io\github\typhon0\AnimateFX\1.2.3\AnimateFX-1.2.3.jar"
$cp += ";$m2\de\jensd\fontawesomefx-fontawesome\4.7.0-9.1.2\fontawesomefx-fontawesome-4.7.0-9.1.2.jar"
$cp += ";$m2\de\jensd\fontawesomefx-commons\9.1.2\fontawesomefx-commons-9.1.2.jar"
$cp += ";$m2\org\slf4j\slf4j-api\2.0.7\slf4j-api-2.0.7.jar"
$cp += ";$m2\ch\qos\logback\logback-classic\1.4.8\logback-classic-1.4.8.jar"
$cp += ";$m2\ch\qos\logback\logback-core\1.4.8\logback-core-1.4.8.jar"
$cp += ";$m2\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar"
$cp += ";$m2\org\apache\commons\commons-lang3\3.12.0\commons-lang3-3.12.0.jar"
$cp += ";$m2\org\apache\commons\commons-collections4\4.4\commons-collections4-4.4.jar"

# Build module path for JavaFX
$mp = "$m2\org\openjfx\javafx-base\$jfx\javafx-base-$jfx-win.jar"
$mp += ";$m2\org\openjfx\javafx-controls\$jfx\javafx-controls-$jfx-win.jar"
$mp += ";$m2\org\openjfx\javafx-graphics\$jfx\javafx-graphics-$jfx-win.jar"
$mp += ";$m2\org\openjfx\javafx-fxml\$jfx\javafx-fxml-$jfx-win.jar"
$mp += ";$m2\org\openjfx\javafx-swing\$jfx\javafx-swing-$jfx-win.jar"

# Check if compiled
$mainClass = "target\classes\gui\OptLoadGUI.class"
if (-not (Test-Path $mainClass)) {
    Write-Host "Compiling sources..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path "target\classes" | Out-Null
    
    # Get all Java files
    $javaFiles = @()
    Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | ForEach-Object {
        $javaFiles += $_.FullName
    }
    
    # Compile
    & javac -d "target\classes" -cp $cp -sourcepath "src\main\java" $javaFiles
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compilation failed" -ForegroundColor Red
        exit 1
    }
    Write-Host "Compiled successfully" -ForegroundColor Green
}

# Launch
Write-Host "Starting OptLoad GUI..." -ForegroundColor Green
Write-Host ""

& java --module-path $mp --add-modules javafx.controls,javafx.fxml,javafx.swing -cp $cp gui.OptLoadGUI

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Error occurred" -ForegroundColor Red
    exit $LASTEXITCODE
}
