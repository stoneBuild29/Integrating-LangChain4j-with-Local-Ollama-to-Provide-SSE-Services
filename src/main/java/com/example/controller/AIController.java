package com.ajjtcx.controller;

import com.ajjtcx.client.OllamaLangChainClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/ai")
public class AIController {
    private final OllamaLangChainClient ollamaClient;

    // 构造函数注入（确保 Spring 管理 Bean）
    public AIController(OllamaLangChainClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    @GetMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateStream(@RequestParam String prompt) {
        return ollamaClient.generateStream(prompt);
    }
}