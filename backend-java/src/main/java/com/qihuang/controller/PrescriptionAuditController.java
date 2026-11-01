package com.qihuang.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.qihuang.model.AuditRequest;
import com.qihuang.model.AuditResult;
import com.qihuang.model.HerbItem;
import com.qihuang.service.AiServiceClient;
import com.qihuang.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中药处方配伍安全质检与跨微服务调度控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrescriptionAuditController {

    private final RuleEngineService ruleEngineService;
    private final AiServiceClient aiServiceClient;

    /**
     * 处方文本结构化列表直接审查接口
     */
    @PostMapping("/prescription-text")
    public Map<String, Object> auditPrescriptionText(@RequestBody AuditRequest request) {
        AuditResult result = ruleEngineService.auditPrescription(request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        return response;
    }

    /**
     * 处方图片全流程智能质检调度管道：
     * 1. 接收前端上传的处方图片
     * 2. 跨进程调度 Python RapidOCR 服务解析图片抽取中药实体
     * 3. 实体结构化对齐后，直接灌入 Java 纯内存配伍规则引擎
     * 4. 汇总识别结果与安全质检报告一并返回前端
     */
    @PostMapping("/prescription-image")
    public Map<String, Object> auditPrescriptionImage(@RequestParam("file") MultipartFile file) {
        long startTime = System.currentTimeMillis();

        // 步骤 1: 调度 Python AI 节点执行 OCR 解析
        JSONObject ocrData = aiServiceClient.ocrPrescription(file);

        // 步骤 2: 提取并解析中药药味
        List<HerbItem> herbList = new ArrayList<>();
        if (ocrData != null && ocrData.containsKey("herbs")) {
            JSONArray herbsArr = ocrData.getJSONArray("herbs");
            for (int i = 0; i < herbsArr.size(); i++) {
                JSONObject h = herbsArr.getJSONObject(i);
                String name = h.getString("name");
                Double dose = h.getDouble("dose");
                String method = h.getString("processing");
                if (method == null || method.isBlank()) {
                    method = h.getString("method");
                }
                herbList.add(HerbItem.builder()
                        .name(name)
                        .dose(dose != null ? dose : 6.0)
                        .decoctionMethod(method)
                        .build());
            }
        }

        // 步骤 3: 调度 Java 规则引擎执行毫秒级安全质检
        AuditRequest auditRequest = AuditRequest.builder().herbs(herbList).build();
        AuditResult auditResult = ruleEngineService.auditPrescription(auditRequest);

        long totalLatencyMs = System.currentTimeMillis() - startTime;
        log.info("处方全流程质检完成，总耗时: {}ms，包含药味数: {}，安全评级: {}", 
                totalLatencyMs, herbList.size(), auditResult.getStatus());

        Map<String, Object> data = new HashMap<>();
        data.put("rawText", ocrData != null ? ocrData.getString("raw_text") : "");
        data.put("herbs", herbList);
        data.put("auditResult", auditResult);
        data.put("pipelineLatencyMs", totalLatencyMs);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }

    /**
     * 大模型 + RAG 深度方解推演接口（君臣佐使与古籍病机剖析）
     */
    @PostMapping("/ai-formula-analysis")
    public Map<String, Object> getAiFormulaAnalysis(@RequestBody Map<String, Object> request) {
        List<?> herbs = (List<?>) request.get("herbs");
        Object auditResult = request.get("auditResult");
        JSONObject analysis = aiServiceClient.getFormulaDeepAnalysis(herbs, auditResult);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", analysis);
        return response;
    }

    /**
     * 获取系统内置规则库概要统计（供看板展示）
     */
    @GetMapping("/rules/summary")
    public Map<String, Object> getRulesSummary() {
        Map<String, Object> data = new HashMap<>();
        data.put("shiBaFanPairs", "3大经典冲突组 (甘草反组、乌头反组、藜芦反组)");
        data.put("shiJiuWeiCount", 10);
        data.put("dosageLimitCount", 6);
        data.put("decoctionTypes", "先煎、后下、包煎、烊化");
        data.put("engineType", "In-Memory Set Intersection (纯内存高并发引擎)");
        data.put("avgLatencyMs", "< 5ms");

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }
}
