package com.qihuang.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 跨微服务 AI 算力调用中枢客户端 (Java -> Python :8000)
 * 统一调度 Python 内部 OCR 推理、数字舌象特征分析、RAG 古籍检索与大模型深度方解服务
 */
@Slf4j
@Service
public class AiServiceClient {

    @org.springframework.beans.factory.annotation.Value("${ai-service.base-url:http://127.0.0.1:8000}")
    private String pythonAiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 探活 Python AI 算力服务
     */
    public boolean isAiServiceAlive() {
        try {
            ResponseEntity<String> res = restTemplate.getForEntity(pythonAiBaseUrl + "/health", String.class);
            return res.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("Python AI 算力节点未在线或连接超时: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 转发处方图片至 Python RapidOCR 进行实体抽取
     */
    public JSONObject ocrPrescription(MultipartFile file) {
        String url = pythonAiBaseUrl + "/api/ai/ocr/prescription";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "prescription.jpg";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject json = JSON.parseObject(response.getBody());
                if (json.getIntValue("code") == 200) {
                    return json.getJSONObject("data");
                }
            }
        } catch (Exception e) {
            log.warn("调用 Python OCR 服务异常，启用智能降级兜底: {}", e.getMessage());
        }

        // 智能降级兜底 (如模拟识别)
        JSONObject fallback = new JSONObject();
        fallback.put("raw_text", "生甘草 6g 甘遂 3g 炒白术 15g 茯苓 12g");
        fallback.put("herbs", List.of(
                Map.of("name", "生甘草", "dose", 6.0, "unit", "g"),
                Map.of("name", "甘遂", "dose", 3.0, "unit", "g"),
                Map.of("name", "炒白术", "dose", 15.0, "unit", "g"),
                Map.of("name", "茯苓", "dose", 12.0, "unit", "g")
        ));
        fallback.put("is_fallback", true);
        return fallback;
    }

    /**
     * 转发舌象图片至 Python 进行数字舌象分析
     */
    public JSONObject analyzeTongue(MultipartFile file) {
        String url = pythonAiBaseUrl + "/api/ai/tongue/analyze";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "tongue.jpg";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject json = JSON.parseObject(response.getBody());
                if (json.getIntValue("code") == 200) {
                    return json.getJSONObject("data");
                }
            }
        } catch (Exception e) {
            log.warn("调用 Python 舌诊服务异常，启用智能降级兜底: {}", e.getMessage());
        }

        // 智能降级兜底
        JSONObject fallback = new JSONObject();
        fallback.put("tongueColor", "淡红舌 (正常)");
        fallback.put("mossColor", "薄白润苔");
        fallback.put("toothMarks", "舌体适中无齿痕");
        fallback.put("mainConstitution", "平和质");
        fallback.put("confidence", 0.95);
        fallback.put("radarValues", List.of(90, 20, 15, 20, 25, 20, 15, 20, 15));
        fallback.put("healthAdvice", "气血阴阳调和，体质状态良好。起居顺应四时规律，无需盲目进补。");
        fallback.put("dietRecommendation", "应季五谷果蔬，顺时而食即可。");
        fallback.put("is_fallback", true);
        return fallback;
    }

    /**
     * 调度 Python 大模型 + RAG 生成深度方解与君臣佐使推演
     */
    public JSONObject getFormulaDeepAnalysis(List<?> herbs, Object auditResult) {
        String url = pythonAiBaseUrl + "/api/ai/llm/formula-analysis";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> req = Map.of(
                    "herbs", herbs,
                    "auditResult", auditResult
            );

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject json = JSON.parseObject(response.getBody());
                if (json.getIntValue("code") == 200) {
                    return json.getJSONObject("data");
                }
            }
        } catch (Exception e) {
            log.warn("调用 Python 大模型方解服务异常，启用本地降级兜底: {}", e.getMessage());
        }

        JSONObject fallback = new JSONObject();
        fallback.put("source", "Java 规则网关智能医学知识合成 (离线兜底)");
        fallback.put("analysis", "### 一、 【君臣佐使配伍精义】\n- 本方诸药配伍严整，各司其职，遵循辨证论治原则。\n\n### 二、 【现代药理分析】\n- 组方各有效成分具良好协同调和作用。\n\n### 三、 【调护建议】\n- 宜遵医嘱水煎温服，忌食生冷油腻。");
        fallback.put("is_online", false);
        return fallback;
    }

    /**
     * 调度 Python 大模型进行中医药安全智能问答
     */
    public String chatWithAi(String question, List<String> contextHerbs) {
        String url = pythonAiBaseUrl + "/api/ai/llm/chat";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> req = new HashMap<>();
            req.put("question", question);
            if (contextHerbs != null) {
                req.put("contextHerbs", contextHerbs);
            }

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject json = JSON.parseObject(response.getBody());
                if (json.getIntValue("code") == 200) {
                    return json.getJSONObject("data").getString("reply");
                }
            }
        } catch (Exception e) {
            log.warn("调用 Python 大模型问答服务异常，启用本地问答兜底: {}", e.getMessage());
        }

        return "岐黄盾 AI 助手提示：中药配伍注重七情和合，生克制化。如有疑虑请严格遵循中医师医嘱。（系统正在离线守护中）";
    }
}
