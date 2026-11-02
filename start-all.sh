#!/bin/bash
# ========================================================
#   HerbShield (Qihuang-Shield) Launcher for macOS / Linux
# ========================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$BASE_DIR"

mkdir -p "$BASE_DIR/logs"

# Ensure JDK 21 is used
if [ -d "$HOME/.local/share/jdk-21/Contents/Home" ]; then
    export JAVA_HOME="$HOME/.local/share/jdk-21/Contents/Home"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "========================================================"
echo "       HerbShield (Qihuang-Shield) macOS Launcher"
echo "========================================================"
echo "Java:   $(java -version 2>&1 | head -n 1)"
echo "Python: $($BASE_DIR/backend-python/venv/bin/python --version 2>&1)"
echo "Node:   $(node -v 2>&1)"
echo ""

# 1. Start Python AI service (8000)
if ! lsof -i :8000 >/dev/null 2>&1; then
    echo "[1/3] Starting Python AI Service (Port 8000)..."
    nohup "$BASE_DIR/backend-python/venv/bin/python" "$BASE_DIR/backend-python/main.py" > "$BASE_DIR/logs/python.log" 2>&1 &
else
    echo "[1/3] Python AI Service is already running on Port 8000."
fi

# 2. Start Java Core service (8080)
if ! lsof -i :8080 >/dev/null 2>&1; then
    echo "[2/3] Starting Java Core and Rule Engine (Port 8080)..."
    (cd "$BASE_DIR/backend-java" && nohup mvn spring-boot:run > "$BASE_DIR/logs/java.log" 2>&1 &)
else
    echo "[2/3] Java Core is already running on Port 8080."
fi

# 3. Start Frontend service (3000)
if ! lsof -i :3000 >/dev/null 2>&1; then
    echo "[3/3] Starting Vue 3 Frontend (Port 3000)..."
    (cd "$BASE_DIR/frontend" && nohup "$BASE_DIR/frontend/node_modules/.bin/vite" --host 0.0.0.0 --port 3000 > "$BASE_DIR/logs/frontend.log" 2>&1 &)
else
    echo "[3/3] Vue 3 Frontend is already running on Port 3000."
fi

echo ""
echo "Waiting for microservices to initialize..."
sleep 2

echo "========================================================"
echo " All microservices launched:"
echo "  - Frontend:     http://localhost:3000"
echo "  - Java Gateway: http://localhost:8080/api/health"
echo "  - Python AI:    http://127.0.0.1:8000/docs"
echo "  - Logs saved to: $BASE_DIR/logs/"
echo "========================================================"
echo "Run ./stop-all.sh to terminate all services."
