package com.sejong.recruit.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SejongAuthDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String studentId;
        private String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String major;
        private String studentId;
        private String name;
        private String grade;
        private String status;
        private String completedSemester;
        private String accessToken;
        private String refreshToken;
    }
}
