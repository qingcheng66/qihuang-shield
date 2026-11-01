package com.qihuang.controller;

import com.qihuang.service.AiServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 岐黄 AI 中医药安全与调摄问答控制器
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiChatController {

    private final AiServiceClient aiServiceClient;

    /**
     * 中医药安全与辨证调养智能问答
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> request) {
        String question = (String) request.get("question");
        @SuppressWarnings("unchecked")
        List<String> contextHerbs = (List<String>) request.get("contextHerbs");

        String reply = aiServiceClient.chatWithAi(question, contextHerbs);

        Map<String, Object> data = new HashMap<>();
        data.put("reply", reply);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }
}
