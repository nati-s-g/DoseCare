@echo off
title DoseCare
echo Starting DoseCare...
java -jar DoseCare.jar
if %errorlevel% neq 0 (
    echo.
    echo Error: Could not start the application.
    echo Please make sure Java 25 is installed.
    pause
)
