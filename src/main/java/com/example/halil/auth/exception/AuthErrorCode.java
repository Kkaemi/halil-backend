package com.example.halil.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AuthErrorCode {

    // 로그인 관련
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "해당 이메일의 유저를 찾을 수 없습니다"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),

    // JWT 관련
    TOKEN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 생성 실패"),
    TOKEN_VERIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 검증 실패"),
    CLAIM_EXTRACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 클레임 분석 실패");

    private final HttpStatus httpStatus;
    private final String message;

    public AuthException asException() {
        return new AuthException(message, httpStatus);
    }
}
