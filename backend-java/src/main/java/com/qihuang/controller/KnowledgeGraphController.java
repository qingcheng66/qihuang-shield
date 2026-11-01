package com.qihuang.controller;

import com.qihuang.model.HerbDetail;
import com.qihuang.model.KnowledgeGraphData;
import com.qihuang.service.KnowledgeGraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 知识图谱与古籍本草专论 API 控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KnowledgeGraphController {

    private final KnowledgeGraphService knowledgeGraphService;

    /**
     * 获取全景力导向知识图谱数据
     */
    @GetMapping("/graph/overview")
    public Map<String, Object> getGraphOverview() {
        KnowledgeGraphData data = knowledgeGraphService.getOverviewGraph();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }

    /**
     * 查询指定药材考据详情
     */
    @GetMapping("/graph/herb/{name}")
    public Map<String, Object> getHerbDetail(@PathVariable String name) {
        HerbDetail detail = knowledgeGraphService.getHerbDetail(name);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", detail);
        return response;
    }

    /**
     * 获取系统全量法定规则与禁忌字典详情
     */
    @GetMapping("/rules/all")
    public Map<String, Object> getAllRules() {
        Map<String, Object> rules = knowledgeGraphService.getAllRulesDetail();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", rules);
        return response;
    }
}
