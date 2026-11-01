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
import java.util.List;
import java.util.Map;

/**
 * 跨微服务 AI 算力调用中枢客户端 (Java -> Python :8000)
 * 统一调度 Python 内部 OCR 推理与数字舌象特征分析服务
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
}
