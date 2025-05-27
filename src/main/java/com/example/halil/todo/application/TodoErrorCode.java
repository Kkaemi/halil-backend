package com.example.halil.todo.application;

import com.example.halil.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TodoErrorCode {

    TODO_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 할 일을 찾을 수 없습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public ApiException exception() {
        return new ApiException(message, httpStatus);
    }
}
