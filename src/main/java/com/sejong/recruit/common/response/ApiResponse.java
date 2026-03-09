package com.sejong.recruit.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 전역 공통 응답 객체
 * 모든 API 응답은 이 포맷으로 통일하여 프론트엔드와의 통신을 규격화합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private boolean success; // 성공 여부
    private T data;          // 실제 반환 데이터 (성공 시)
    private ErrorResponse error; // 에러 정보 (실패 시)

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // 성공 응답 (데이터 없음)
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }

    // 실패 응답
    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message));
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorResponse {
        private String code;
        private String message;
    }
}