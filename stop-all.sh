#!/bin/bash
# ========================================================
#   HerbShield (Qihuang-Shield) Stopper for macOS / Linux
# ========================================================

echo "Stopping HerbShield microservices..."

# Kill processes listening on ports 3000, 8000, 8080
for PORT in 3000 8000 8080; do
    PID=$(lsof -ti :$PORT)
    if [ -n "$PID" ]; then
        echo "Killing process on port $PORT (PID: $PID)..."
        kill -9 $PID 2>/dev/null
    fi
done

echo "All HerbShield services stopped."
