package com.example.halil.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenVerificationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

    public TokenVerificationException() {
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = "토큰 검증을 실행 할 수 없습니다";
    }
}
