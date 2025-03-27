package com.ajjtcx.client;

/**
 * @className: OllamaClient
 * @author: Scarlet
 * @date: 2025/3/27
 **/
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;

public class OllamaClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final String baseUrl = "http://172.168.0.93:11434";

    // 发送流式生成请求
    public void generateStream(String model, String prompt, ResponseBodyCallback callback) {
        String json = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": true}", model, prompt);
        Request request = new Request.Builder()
                .url(baseUrl + "/api/generate")
                .post(RequestBody.create(json, JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        BufferedReader reader = new BufferedReader(body.charStream());
                        String line;
                        while ((line = reader.readLine()) != null) {
                            callback.onChunk(line); // 逐行处理流式响应
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }
        });
    }

    public interface ResponseBodyCallback {
        void onChunk(String chunk);
        void onError(IOException e);
    }
}
