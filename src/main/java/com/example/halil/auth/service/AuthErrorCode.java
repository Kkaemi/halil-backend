package com.example.halil.auth.service;

import com.example.halil.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthErrorCode {

    // 로그인 관련
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "해당 이메일의 유저를 찾을 수 없습니다"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),
    USER_DELETED(HttpStatus.BAD_REQUEST, "탈퇴한 회원입니다"),

    // JWT 관련
    TOKEN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 생성 실패"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다"),

    // 메일 관련
    MAIL_DELIVERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송 실패");

    private final HttpStatus httpStatus;
    private final String message;

    public ApiException exception() {
        return new ApiException(message, httpStatus);
    }
}
