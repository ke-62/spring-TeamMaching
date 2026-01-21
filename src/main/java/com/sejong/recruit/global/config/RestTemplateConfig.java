package com.sejong.recruit.global.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 외부 API(AI 모델 서버)와의 통신을 위한 RestTemplate 설정
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10)) // 연결 타임아웃 10초
                .setReadTimeout(Duration.ofSeconds(30))    // 읽기 타임아웃 30초 (AI 분석 시간을 고려하여 넉넉히 설정)
                .build();
    }
}