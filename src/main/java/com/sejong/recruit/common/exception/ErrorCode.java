package com.sejong.recruit.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 에러 코드 정의
 * 각 도메인별로 에러 코드를 분리하여 관리할 수 있습니다.
 */
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

    // Auth
    PORTAL_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_001", "포털 로그인에 실패했습니다."),
    PORTAL_COMMUNICATION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AUTH_002", "포털 서버와 통신 중 오류가 발생했습니다."),
    PORTAL_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_003", "포털 정보 파싱에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}