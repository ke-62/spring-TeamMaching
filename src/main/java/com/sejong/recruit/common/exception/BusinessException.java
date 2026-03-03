package com.sejong.recruit.common.exception;

import lombok.Getter;

/**
 * 애플리케이션의 비즈니스 로직 중 발생하는 예외들을 처리하기 위한 기본 예외 클래스
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}