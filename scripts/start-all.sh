#!/usr/bin/env bash
set -e

PROJECT_ROOT="/www/wwwroot/qihuang-shield"

echo "=== [1/3] 检查并重载宝塔 Nginx ==="
sudo /etc/init.d/nginx reload || sudo /etc/init.d/nginx start

echo "=== [2/3] 检查并启动 Python AI 服务 (Port 8001) ==="
cd "$PROJECT_ROOT/backend-python"
if pgrep -f "backend-python.*main.py" > /dev/null; then
    echo "Python AI 服务已在运行"
else
    PORT=8001 nohup ./venv/bin/python3 main.py > nohup_py.log 2>&1 &
    echo $! > py.pid
    echo "Python AI 服务已启动 (PID: $(cat py.pid))"
fi

echo "=== [3/3] 检查并启动 Java 核心引擎 (Port 8081) ==="
cd "$PROJECT_ROOT/backend-java"
if pgrep -f "herb-shield-backend" > /dev/null; then
    echo "Java 后端已在运行"
else
    nohup java -Xms128m -Xmx384m -XX:+UseG1GC \
      -jar target/herb-shield-backend-1.0.0-SNAPSHOT.jar \
      --server.port=8081 \
      --ai-service.base-url=http://127.0.0.1:8001 \
      > nohup_java.log 2>&1 &
    echo $! > java.pid
    echo "Java 后端已启动 (PID: $(cat java.pid))"
fi

echo "=== 岐黄盾所有服务监听端口核查 ==="
sleep 2
sudo ss -tulpn | grep -E '8001|8081|3001'
echo "服务全部就绪！前端请访问端口: 3001"
