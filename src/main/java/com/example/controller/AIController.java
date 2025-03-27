package com.ajjtcx.controller;

/**
 * @className: AIController
 * @author: Scarlet
 * @date: 2025/3/27
 **/
import com.ajjtcx.client.OllamaClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AIController {
    private final OllamaClient ollamaClient = new OllamaClient();

    @GetMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateStream(@RequestParam String prompt) {
        SseEmitter emitter = new SseEmitter(60_000L); // 60秒超时

        ollamaClient.generateStream("deepseek-r1:1.5b", prompt, new OllamaClient.ResponseBodyCallback() {
            @Override
            public void onChunk(String chunk) {
                try {
                    // 解析 Ollama 返回的 JSON
                    String response = parseChunk(chunk);
                    emitter.send(SseEmitter.event().data(response));
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onError(IOException e) {
                emitter.completeWithError(e);
            }
        });

        emitter.onCompletion(() -> System.out.println("SSE completed"));
        return emitter;
    }

    private String parseChunk(String jsonChunk) {
        // 使用 Jackson/Gson 解析 JSON，提取 "response" 字段
        // 示例：{"response": "Hello", "done": false}
        return jsonChunk.replaceAll(".*\"response\":\"(.*?)\".*", "$1");
    }
}
