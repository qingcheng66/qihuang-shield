@echo off
title HerbShield Service Stopper
cd /d "%~dp0"

echo ========================================================
echo        HerbShield - Stop All Microservices
echo ========================================================
echo.

echo Cleaning up ports 3000, 8000, 8080...
powershell -NoProfile -Command "Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue | Where-Object { $_.LocalPort -in 3000, 8000, 8080 } | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue; Write-Host ('Stopped PID ' + $_.OwningProcess + ' on port ' + $_.LocalPort) }"

echo.
echo All microservices stopped successfully.
echo ========================================================
pause
