# Run script for Student Grade Management System
$ErrorActionPreference = "Stop"

Set-Location $PSScriptRoot

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Student Grade Management System - Chuka University" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# Ensure bin directory exists
if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

# Compile
Write-Host "[1/2] Compiling source files..." -ForegroundColor Yellow
javac -d bin -sourcepath GradeSystem\src GradeSystem\src\com\chuka\gradesystem\gui\MainFrame.java

# Launch
Write-Host "[2/2] Launching application..." -ForegroundColor Green
Start-Process java -ArgumentList "-cp bin com.chuka.gradesystem.gui.MainFrame"

Write-Host "Done! The application window is open." -ForegroundColor Cyan
