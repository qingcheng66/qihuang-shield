# QiHuang Shield - PowerShell One-Click Launcher
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "       岐黄盾 (HerbShield) - 微服务一键启动 (PowerShell)" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/3] 正在启动 Python AI 计算中枢 (:8000)..." -ForegroundColor Yellow
Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$scriptDir\backend-python`" && python main.py"

Write-Host "[2/3] 正在启动 Java 业务中枢与规则引擎 (:8080)..." -ForegroundColor Yellow
Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$scriptDir\backend-java`" && mvn spring-boot:run"

Write-Host "[3/3] 正在启动 Vue 3 PWA 前端工作台 (:3000)..." -ForegroundColor Yellow
Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$scriptDir\frontend`" && npm run dev -- --host 0.0.0.0 --port 3000"

Write-Host ""
Write-Host "等待服务初始化，5 秒后呼出浏览器..." -ForegroundColor Green
Start-Sleep -Seconds 5
Start-Process "http://127.0.0.1:3000"

Write-Host "========================================================" -ForegroundColor Green
Write-Host " 所有微服务已在独立窗口运行:" -ForegroundColor Green
Write-Host "  - 前端工作台: http://127.0.0.1:3000"
Write-Host "  - Java 网关中枢: http://127.0.0.1:8080/api/health"
Write-Host "  - Python AI 算力: http://127.0.0.1:8000/health"
Write-Host "========================================================" -ForegroundColor Green
