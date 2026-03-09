package com.sejong.recruit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * AI 서버(Colab 등)와 실제 통신을 담당하는 클라이언트
 */
@Component
@RequiredArgsConstructor
public class AiClient {

    private final RestTemplate restTemplate;

    @Value("${ai.api.url}")
    private String aiServerUrl;

    /**
     * AI 서버 상태 체크
     */
    public String checkAiStatus() {
        try {
            return restTemplate.getForObject(aiServerUrl + "/health", String.class);
        } catch (Exception e) {
            return "AI 서버 연결 실패: " + e.getMessage();
        }
    }
}
