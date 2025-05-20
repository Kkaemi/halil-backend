package com.example.halil.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenGenerationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

    public TokenGenerationException() {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        message = "토큰 생성 실패";
    }
}
