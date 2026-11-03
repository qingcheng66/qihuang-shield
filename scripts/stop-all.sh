#!/usr/bin/env bash
PROJECT_ROOT="/www/wwwroot/qihuang-shield"

echo "=== 停止 Java 后端 ==="
pkill -f "herb-shield-backend" || true
rm -f "$PROJECT_ROOT/backend-java/java.pid"

echo "=== 停止 Python AI 服务 ==="
pkill -f "backend-python.*main.py" || true
rm -f "$PROJECT_ROOT/backend-python/py.pid"

echo "=== 岐黄盾业务服务已停止 ==="
