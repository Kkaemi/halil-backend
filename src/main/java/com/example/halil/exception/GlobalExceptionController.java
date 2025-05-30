package com.example.halil.exception;

import com.example.halil.user.domain.exception.EmailDuplicateException;
import com.example.halil.user.domain.exception.PasswordMismatchException;
import com.example.halil.user.domain.exception.PasswordReusedException;
import com.example.halil.user.domain.exception.UserStatusException;
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
        return ResponseEntity.status(e.getHttpStatus()).body(new CommonErrorResponse(
                e.getHttpStatus().value(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler({
            PasswordMismatchException.class,
            PasswordReusedException.class,
            UserStatusException.class,
            EmailDuplicateException.class
    })
    ResponseEntity<CommonErrorResponse> handleUserDomainException(
            Exception e,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
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
