@echo off
title DoseCare Launcher
set "JAR_PATH=target\DoseCare-1.0-SNAPSHOT.jar"

echo Starting DoseCare Application...
echo.

REM Check if Java is installed
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in your PATH.
    echo Please install Java 25 or later.
    pause
    exit /b 1
)

REM Check if JAR exists, build if not
if not exist "%JAR_PATH%" (
    echo Fat JAR not found. Building project...
    call mvn clean package
    if %errorlevel% neq 0 (
        echo Build failed!
        pause
        exit /b 1
    )
)

echo Launching DoseCare...
java -jar "%JAR_PATH%"

if %errorlevel% neq 0 (
    echo Application crashed with error code %errorlevel%
    pause
)
