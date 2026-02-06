package com.sejong.recruit.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

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

    private final List<TestAccount> testAccounts = new ArrayList<>();

    @PostConstruct
    public void init() {
        // application.yml의 테스트 계정 직접 등록
        testAccounts.add(new TestAccount("20211234", "test1234", "이고은", "컴퓨터공학과", 3));
        testAccounts.add(new TestAccount("20211235", "test1234", "황채영", "소프트웨어학과", 3));
    }

    public Map<String, String> authenticateAndGetStudentInfo(String studentId, String password) {
        if (devMode) {
            return devModeAuthenticate(studentId, password);
        }
        // 실제 포털 연동은 추후 구현
        log.warn("실제 포털 연동은 아직 구현되지 않았습니다.");
        return null;
    }

    private Map<String, String> devModeAuthenticate(String studentId, String password) {
        // 등록된 테스트 계정 확인
        for (TestAccount account : testAccounts) {
            if (account.getStudentId().equals(studentId) && account.getPassword().equals(password)) {
                Map<String, String> info = new HashMap<>();
                info.put("name", account.getName());
                info.put("department", account.getDepartment());
                info.put("grade", String.valueOf(account.getGrade()));
                log.info("Dev mode 인증 성공: {} ({})", account.getName(), studentId);
                return info;
            }
        }

        // 등록되지 않은 학번이면 자동 계정 생성 (dev-mode)
        if (password.equals("test1234")) {
            Map<String, String> info = new HashMap<>();
            info.put("name", "세종학생_" + studentId);
            info.put("department", "미지정학과");
            info.put("grade", "1");
            log.info("Dev mode 자동 계정 생성: {}", studentId);
            return info;
        }

        log.warn("Dev mode 인증 실패: {}", studentId);
        return null;
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
