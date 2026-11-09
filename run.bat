@echo off
setlocal
cd /d "%~dp0"

echo ========================================================
echo   Student Grade Management System - Chuka University
echo ========================================================
echo.

:: Ensure bin directory exists
if not exist "bin" mkdir "bin"

:: Compile all source files
echo [1/2] Compiling source files...
javac -d bin -sourcepath GradeSystem\src GradeSystem\src\com\chuka\gradesystem\gui\MainFrame.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Please make sure Java JDK is installed and added to PATH.
    pause
    exit /b %ERRORLEVEL%
)

:: Launch the application
echo [2/2] Launching application...
start java -cp bin com.chuka.gradesystem.gui.MainFrame

echo Done! The application window is open.
