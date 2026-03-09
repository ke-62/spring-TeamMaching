package com.sejong.recruit.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SejongPortalService {

    @Value("${sejong.portal.dev-mode:true}")
    private boolean devMode;

    @Value("${sejong.portal.auth-service-url:http://localhost:5001/auth}")
    private String authServiceUrl;

    private final List<TestAccount> testAccounts = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        // 테스트 계정 없음
    }

    public Map<String, String> authenticateAndGetStudentInfo(String studentId, String password) {
        // 1. 테스트 계정 먼저 확인 (dev-mode 여부와 관계없이)
        Map<String, String> testResult = testAccountAuthenticate(studentId, password);
        if (testResult != null) {
            return testResult;
        }

        // 2. dev-mode이고 테스트 계정이 아닌 경우 → 자동 생성 (기존 동작 유지)
        if (devMode) {
            Map<String, String> info = new HashMap<>();
            info.put("name", "세종학생_" + studentId);
            info.put("department", "미지정학과");
            info.put("grade", "1");
            log.info("Dev mode 자동 계정 생성: {}", studentId);
            return info;
        }

        // 3. 실제 모드 → Python 인증 서비스 호출
        return portalAuthenticate(studentId, password);
    }

    private Map<String, String> testAccountAuthenticate(String studentId, String password) {
        for (TestAccount account : testAccounts) {
            if (account.getStudentId().equals(studentId) && account.getPassword().equals(password)) {
                Map<String, String> info = new HashMap<>();
                info.put("name", account.getName());
                info.put("department", account.getDepartment());
                info.put("grade", String.valueOf(account.getGrade()));
                log.info("테스트 계정 인증 성공: {} ({})", account.getName(), studentId);
                return info;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> portalAuthenticate(String studentId, String password) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("id", studentId);
            requestBody.put("password", password);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    authServiceUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body != null && Boolean.TRUE.equals(body.get("success"))) {
                Map<String, String> info = new HashMap<>();
                info.put("name", String.valueOf(body.getOrDefault("name", "")));
                info.put("department", String.valueOf(body.getOrDefault("major", "")));
                log.info("포털 인증 성공: {} ({})", info.get("name"), studentId);
                return info;
            } else {
                String message = body != null ? String.valueOf(body.getOrDefault("message", "인증 실패")) : "인증 실패";
                log.warn("포털 인증 실패 ({}): {}", studentId, message);
                return null;
            }
        } catch (Exception e) {
            log.error("Python 인증 서비스 호출 실패: {}", e.getMessage());
            return null;
        }
    }

    @Getter
    private static class TestAccount {
        private final String studentId;
        private final String password;
        private final String name;
        private final String department;
        private final int grade;

        public TestAccount(String studentId, String password, String name, String department, int grade) {
            this.studentId = studentId;
            this.password = password;
            this.name = name;
            this.department = department;
            this.grade = grade;
        }
    }
}
