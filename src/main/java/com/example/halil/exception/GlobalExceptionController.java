package com.example.halil.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<CommonErrorResponse> handleApiException(
            ApiException e,
            HttpServletRequest request
    ) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.status(e.getHttpStatus()).body(new CommonErrorResponse(
                e.getHttpStatus().value(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<CommonErrorResponse> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        log.error("예상하지 못한 예외 발생", e);

        return ResponseEntity.internalServerError().body(new CommonErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        ));
    }
}
