@echo off
echo Starting CoreVax Application...
echo.

if not exist target\CoreVax-1.0-SNAPSHOT.jar (
    echo Fat JAR not found. Building project...
    call mvn clean package
    if errorlevel 1 (
        echo Build failed!
        pause
        exit /b 1
    )
)

echo Launching CoreVax...
java -jar target\CoreVax-1.0-SNAPSHOT.jar
if errorlevel 1 (
    echo Application crashed with error code %errorlevel%
)
pause