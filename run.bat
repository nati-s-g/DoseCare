@echo off
title DoseCare
echo Starting DoseCare...

REM Check for Java 25
java -version 2>&1 | findstr "version \"25" >nul
if %errorlevel% neq 0 (
    echo Warning: Java 25 was not detected explicitly. Attempting to run anyway...
)

echo Launching application...
if exist DoseCare.jar (
    java -jar DoseCare.jar
) else (
    echo Error: DoseCare.jar not found!
    echo Please make sure the application is installed correctly.
    pause
    exit /b 1
)

if %errorlevel% neq 0 (
    echo.
    echo Error: Application exited with code %errorlevel%
    echo Ensure Java 25 is installed.
    pause
)
