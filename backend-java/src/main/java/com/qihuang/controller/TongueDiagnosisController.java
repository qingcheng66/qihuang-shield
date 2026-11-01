package com.qihuang.controller;

import com.alibaba.fastjson2.JSONObject;
import com.qihuang.service.AiServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 数字舌象辨识与中医体质分析跨服务调度控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/tongue")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TongueDiagnosisController {

    private final AiServiceClient aiServiceClient;

    /**
     * 舌象图片上传与体质辨析全流程调度
     */
    @PostMapping("/diagnose")
    public Map<String, Object> diagnoseTongue(@RequestParam("file") MultipartFile file) {
        long startTime = System.currentTimeMillis();

        // 调度 Python 视觉分析节点
        JSONObject tongueData = aiServiceClient.analyzeTongue(file);

        long latency = System.currentTimeMillis() - startTime;
        log.info("数字舌诊特征计算完成，耗时: {}ms", latency);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", tongueData);
        response.put("latencyMs", latency);
        return response;
    }
}
