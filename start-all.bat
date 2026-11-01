@echo off
chcp 65001 >nul
echo ========================================================
echo        岐黄盾 (HerbShield) - 微服务一键联调启动脚本
echo ========================================================
echo.

echo [1/3] 启动 Python AI 计算中枢 (:8000)...
start "HerbShield - Python AI Service" cmd /k "cd /d %~dp0backend-python && python main.py"

echo [2/3] 启动 Java 业务中枢与规则引擎 (:8080)...
start "HerbShield - Java Core & Rule Engine" cmd /k "cd /d %~dp0backend-java && mvn spring-boot:run"

echo [3/3] 启动 Vue 3 PWA 前端服务 (:3000)...
start "HerbShield - Vue 3 Frontend" cmd /k "cd /d %~dp0frontend && npm run dev"

echo.
echo ========================================================
echo  所有微服务正在并发启动中:
echo   - 前端工作台: http://localhost:3000
echo   - Java 网关中枢: http://localhost:8080/api/health
echo   - Python AI 算力: http://127.0.0.1:8000/health
echo ========================================================
echo.
pause
