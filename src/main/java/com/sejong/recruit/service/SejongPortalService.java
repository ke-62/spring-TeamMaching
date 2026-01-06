package com.sejong.recruit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SejongPortalService {

    @Value("${sejong.portal.dev-mode:true}")
    private boolean devMode;

    @Value("${sejong.portal.base-url}")
    private String baseUrl;

    /**
     * 세종대학교 포털 로그인 및 학생 정보 조회
     *
     * 현재는 개발 모드로 동작 (키보드 보안 때문에 실제 포털 크롤링 불가)
     * 실제 배포 시에는 세종대 공식 SSO API를 사용해야 함
     */
    public StudentInfo authenticateAndGetStudentInfo(String studentId, String password) {
        if (devMode) {
            log.info("개발 모드: 테스트 계정으로 로그인 시도 - {}", studentId);
            return authenticateTestAccount(studentId, password);
        } else {
            log.warn("실제 포털 연동은 키보드 보안 문제로 구현 불가");
            log.warn("세종대 공식 SSO API 사용 또는 OAuth 연동 필요");
            throw new RuntimeException("실제 포털 연동은 현재 지원되지 않습니다. 관리자에게 문의하세요.");
        }
    }

    /**
     * 개발/테스트용 계정 인증
     */
    private StudentInfo authenticateTestAccount(String studentId, String password) {
        // 테스트 계정 목록
        Map<String, TestAccount> testAccounts = new HashMap<>();

        // 기본 테스트 계정
        testAccounts.put("20211234", new TestAccount("20211234", "test1234", "이고은", "컴퓨터공학과", 3));
        testAccounts.put("20211235", new TestAccount("20211235", "test1234", "황채영", "소프트웨어학과", 3));
        testAccounts.put("20201111", new TestAccount("20201111", "test1234", "김세종", "인공지능학과", 4));
        testAccounts.put("20221234", new TestAccount("20221234", "test1234", "박대양", "데이터사이언스학과", 2));

        // 계정 확인
        TestAccount account = testAccounts.get(studentId);

        if (account == null) {
            // 등록되지 않은 학번은 자동으로 더미 계정 생성
            log.info("등록되지 않은 학번 {}에 대해 더미 계정 생성", studentId);
            return StudentInfo.builder()
                    .studentId(studentId)
                    .name("테스트 사용자")
                    .department("컴퓨터공학과")
                    .grade(3)
                    .email(studentId + "@sju.ac.kr")
                    .build();
        }

        // 비밀번호 확인
        if (!account.password.equals(password)) {
            log.error("테스트 계정 비밀번호 불일치: {}", studentId);
            throw new RuntimeException("학번 또는 비밀번호가 일치하지 않습니다.");
        }

        log.info("테스트 계정 로그인 성공: {} ({})", account.name, account.department);

        return StudentInfo.builder()
                .studentId(account.studentId)
                .name(account.name)
                .department(account.department)
                .grade(account.grade)
                .email(account.studentId + "@sju.ac.kr")
                .build();
    }

    /**
     * 테스트 계정 정보
     */
    private static class TestAccount {
        String studentId;
        String password;
        String name;
        String department;
        Integer grade;

        TestAccount(String studentId, String password, String name, String department, Integer grade) {
            this.studentId = studentId;
            this.password = password;
            this.name = name;
            this.department = department;
            this.grade = grade;
        }
    }

    /**
     * 학생 정보 DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class StudentInfo {
        private String studentId;
        private String name;
        private String department;
        private Integer grade;
        private String email;
    }
}
