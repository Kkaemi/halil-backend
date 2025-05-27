package com.example.halil.user.service;

import com.example.halil.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다"),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.BAD_REQUEST, "해당 이메일의 유저를 찾을 수 없습니다"),
    USER_NOT_FOUND_BY_ID(HttpStatus.INTERNAL_SERVER_ERROR, "해당 아이디의 유저를 찾을 수 없습니다"),
    PASSWORD_CANNOT_BE_REUSED(HttpStatus.BAD_REQUEST, "이전 비밀번호를 재사용할 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;

    public ApiException exception() {
        return new ApiException(message, httpStatus);
    }
}
