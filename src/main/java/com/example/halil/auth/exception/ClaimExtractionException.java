package com.example.halil.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClaimExtractionException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

    public ClaimExtractionException() {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        message = "토큰 클레임 분석을 실행 할 수 없습니다";
    }
}
