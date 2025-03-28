package com.ajjtcx.client;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Component
@Configuration
public class OllamaLangChainClient {
    private static final String OLLAMA_BASE_URL = "http://172.168.0.93:11434";
    private static final String MODEL_NAME = "deepseek-r1:1.5b";

    // 流式模型 Bean（供其他组件注入）
    @Bean
    public OllamaStreamingChatModel streamingChatModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(OLLAMA_BASE_URL)
                .modelName(MODEL_NAME)
                .temperature(0.7)
                .build();
    }

    // 流式生成方法（关键修改）
    public SseEmitter generateStream(String prompt) {
        SseEmitter emitter = new SseEmitter(60_000L); // 60秒超时

        // 注册流式响应处理器
        streamingChatModel().generate(prompt, new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                try {
                    emitter.send(SseEmitter.event()
                            .data(token)
                            .id(UUID.randomUUID().toString())
                            .comment("Partial response"));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                // Process the final response
                TokenUsage usage = response.tokenUsage();
                try {
                    emitter.send(SseEmitter.event()
                            .data("[DONE] Tokens used")  // See note below
                            .name("complete"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                emitter.complete();
            }

            @Override
            public void onError(Throwable error) {
                emitter.completeWithError(error);
            }
        });

        return emitter;
    }
}