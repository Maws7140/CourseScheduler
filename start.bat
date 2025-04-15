@echo off
echo Starting CourseScheduler...

REM Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Java is not installed or not in PATH.
    echo Please see RUN_ME.md for instructions on installing Java.
    pause
    exit /b 1
)

REM Run the application
java -cp "lib\derby.jar;." MainFrame

if %ERRORLEVEL% NEQ 0 (
    echo Application exited with an error.
    pause
)

exit /b %ERRORLEVEL%