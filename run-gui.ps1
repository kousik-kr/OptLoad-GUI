# OptLoad GUI Launcher for Windows
# Runs the JavaFX application with dependencies from Maven local repository

Write-Host "╔══════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                                              ║" -ForegroundColor Cyan
Write-Host "║       OptLoad - VRP Optimization GUI         ║" -ForegroundColor Cyan
Write-Host "║              Java 21 Edition                 ║" -ForegroundColor Cyan
Write-Host "║                                              ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Set paths
$m2Repo = "$env:USERPROFILE\.m2\repository"
$javafxVersion = "11.0.2"
$os = "win"

# Build classpath with all dependencies
$classpath = @(
    "target\classes"
    "$m2Repo\org\openjfx\javafx-base\$javafxVersion\javafx-base-$javafxVersion.jar"
    "$m2Repo\org\openjfx\javafx-base\$javafxVersion\javafx-base-$javafxVersion-$os.jar"
    "$m2Repo\org\openjfx\javafx-controls\$javafxVersion\javafx-controls-$javafxVersion.jar"
    "$m2Repo\org\openjfx\javafx-controls\$javafxVersion\javafx-controls-$javafxVersion-$os.jar"
    "$m2Repo\org\openjfx\javafx-graphics\$javafxVersion\javafx-graphics-$javafxVersion.jar"
    "$m2Repo\org\openjfx\javafx-graphics\$javafxVersion\javafx-graphics-$javafxVersion-$os.jar"
    "$m2Repo\org\openjfx\javafx-fxml\$javafxVersion\javafx-fxml-$javafxVersion.jar"
    "$m2Repo\org\openjfx\javafx-fxml\$javafxVersion\javafx-fxml-$javafxVersion-$os.jar"
    "$m2Repo\org\openjfx\javafx-swing\$javafxVersion\javafx-swing-$javafxVersion.jar"
    "$m2Repo\org\openjfx\javafx-swing\$javafxVersion\javafx-swing-$javafxVersion-$os.jar"
    "$m2Repo\org\controlsfx\controlsfx\11.1.2\controlsfx-11.1.2.jar"
    "$m2Repo\io\github\typhon0\AnimateFX\1.2.3\AnimateFX-1.2.3.jar"
    "$m2Repo\de\jensd\fontawesomefx-fontawesome\4.7.0-9.1.2\fontawesomefx-fontawesome-4.7.0-9.1.2.jar"
    "$m2Repo\de\jensd\fontawesomefx-commons\9.1.2\fontawesomefx-commons-9.1.2.jar"
    "$m2Repo\org\slf4j\slf4j-api\2.0.7\slf4j-api-2.0.7.jar"
    "$m2Repo\ch\qos\logback\logback-classic\1.4.8\logback-classic-1.4.8.jar"
    "$m2Repo\ch\qos\logback\logback-core\1.4.8\logback-core-1.4.8.jar"
    "$m2Repo\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar"
    "$m2Repo\org\apache\commons\commons-lang3\3.12.0\commons-lang3-3.12.0.jar"
    "$m2Repo\org\apache\commons\commons-collections4\4.4\commons-collections4-4.4.jar"
) -join ";"

# Compile if needed
if (-not (Test-Path "target/classes/gui/OptLoadGUI.class")) {
    Write-Host "Compiling Java sources..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path "target/classes" | Out-Null
    
    $sourceFiles = Get-ChildItem -Path "src/main/java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
    javac -d "target/classes" -cp $classpath -sourcepath "src/main/java" $sourceFiles
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compilation failed!" -ForegroundColor Red
        exit 1
    }
    Write-Host "Compilation successful!" -ForegroundColor Green
}

# Run the application
Write-Host "Starting OptLoad GUI..." -ForegroundColor Green
Write-Host ""

$javaArgs = @(
    "-cp", $classpath,
    "--add-modules", "javafx.controls,javafx.fxml,javafx.swing",
    "--add-exports", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
    "gui.OptLoadGUI"
)

java $javaArgs

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Application exited with errors" -ForegroundColor Red
    exit $LASTEXITCODE
}
