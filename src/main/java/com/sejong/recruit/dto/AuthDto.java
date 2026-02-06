package com.sejong.recruit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String studentId;
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private UserDto user;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshRequest {
        private String refreshToken;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
    }
}
