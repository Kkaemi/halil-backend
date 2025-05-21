package com.example.halil.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailDuplicateException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    public EmailDuplicateException() {
        this.message = "이미 존재하는 이메일입니다";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
