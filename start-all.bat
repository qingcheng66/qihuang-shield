@echo off
title HerbShield Microservices Launcher
cd /d "%~dp0"

echo ========================================================
echo        HerbShield (Qihuang-Shield) Launcher
echo ========================================================
echo.

echo [1/3] Starting Python AI Service (Port 8000)...
start "HerbShield - Python AI Service (:8000)" /D "%~dp0backend-python" cmd /k "python main.py"

echo [2/3] Starting Java Core and Rule Engine (Port 8080)...
start "HerbShield - Java Core (:8080)" /D "%~dp0backend-java" cmd /k "mvn spring-boot:run"

echo [3/3] Starting Vue 3 Frontend (Port 3000)...
start "HerbShield - Vue 3 Frontend (:3000)" /D "%~dp0frontend" cmd /k "npm run dev -- --host 0.0.0.0 --port 3000"

echo.
echo Waiting for services to initialize...
ping -n 5 127.0.0.1 >nul
start http://127.0.0.1:3000

echo ========================================================
echo  All microservices launched in separate windows:
echo   - Frontend: http://127.0.0.1:3000
echo   - Java Gateway: http://127.0.0.1:8080/api/health
echo   - Python AI: http://127.0.0.1:8000/health
echo ========================================================
echo.
pause
