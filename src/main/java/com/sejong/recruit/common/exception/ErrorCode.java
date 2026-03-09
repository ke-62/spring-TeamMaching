package com.sejong.recruit.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_002", "허용되지 않은 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_003", "서버 내부 오류가 발생했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),

    // Project
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_001", "프로젝트를 찾을 수 없습니다."),

    // Application
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APP_001", "지원서를 찾을 수 없습니다."),
    DUPLICATE_APPLICATION(HttpStatus.BAD_REQUEST, "APP_002", "이미 지원한 프로젝트입니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_001", "댓글을 찾을 수 없습니다."),

    // Auth
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_001", "인증에 실패했습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_002", "토큰이 만료되었습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_003", "권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
