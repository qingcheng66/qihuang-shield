package com.qihuang.controller;

import com.qihuang.service.AiServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 微服务集群健康检查探针控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HealthController {

    private final AiServiceClient aiServiceClient;

    @GetMapping("/health")
    public Map<String, Object> health() {
        boolean pythonAlive = aiServiceClient.isAiServiceAlive();

        Map<String, Object> services = new HashMap<>();
        services.put("javaCore", Map.of("status", "UP", "port", 8080, "role", "业务中枢与规则引擎"));
        services.put("pythonAI", Map.of("status", pythonAlive ? "UP" : "STANDBY", "port", 8000, "role", "RapidOCR 与 OpenCV 视觉算力"));

        Map<String, Object> res = new HashMap<>();
        res.put("status", "UP");
        res.put("architecture", "Heterogeneous Microservices (Java 21 + Python 3.12 + Vue 3)");
        res.put("version", "1.0.0");
        res.put("services", services);
        res.put("message", "岐黄盾中医药安全哨兵微服务网关运行正常");
        return res;
    }
}
