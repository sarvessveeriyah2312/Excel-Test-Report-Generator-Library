@echo off
echo ========================================
echo Excel Test Report Generator
echo Generating Sample Report...
echo ========================================
echo.

cd /d "%~dp0"

REM Compile the example if not already compiled
if not exist "examples\SimpleExample.class" (
    echo Compiling example...
    javac -cp "target\excel-test-report-generator-1.0.0.jar" examples\SimpleExample.java
    if errorlevel 1 (
        echo ERROR: Compilation failed!
        pause
        exit /b 1
    )
    echo Compilation successful!
    echo.
)

REM Run the example
echo Running report generator...
java -cp "target\excel-test-report-generator-1.0.0.jar;examples" SimpleExample

if errorlevel 1 (
    echo ERROR: Report generation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! Report generated!
echo Check the current directory for .xlsx file
echo ========================================
echo.

pause
