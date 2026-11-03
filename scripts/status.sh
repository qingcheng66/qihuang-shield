#!/usr/bin/env bash
echo "=== 岐黄盾服务运行状态 ==="
echo "[Nginx 3001]"
sudo ss -tulpn | grep 3001 || echo "未运行"
echo "[Python AI 8001]"
sudo ss -tulpn | grep 8001 || echo "未运行"
echo "[Java Backend 8081]"
sudo ss -tulpn | grep 8081 || echo "未运行"
echo "[Cloudflare Tunnel]"
sudo docker ps --filter "name=qihuang-shield-tunnel" --format "table {{.Names}}\t{{.Status}}"
echo ""
echo "=== 内存与负载 ==="
free -h
uptime
