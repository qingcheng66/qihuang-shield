@echo off
chcp 65001 >nul
echo ========================================================
echo        岐黄盾 (HerbShield) - 一键停止所有微服务
echo ========================================================
echo.

echo 正在清理端口 3000 (前端)、8000 (Python AI)、8080 (Java 后端)...
for %%p in (3000 8000 8080) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%%p ^| findstr LISTENING') do (
        echo 正在终止端口 %%p 上的进程 (PID: %%a)...
        taskkill /f /pid %%a >nul 2>&1
    )
)

echo.
echo 所有微服务已安全退出。
echo ========================================================
pause
