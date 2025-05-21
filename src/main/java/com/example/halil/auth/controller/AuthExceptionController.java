package com.example.halil.auth.controller;

import com.example.halil.auth.domain.AuthException;
import com.example.halil.auth.dto.AuthErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = {AuthController.class})
public class AuthExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<AuthErrorResponse> handleLoginException(
            AuthException e,
            HttpServletRequest request
    ) {
        AuthErrorResponse errorResponse = new AuthErrorResponse(
                e.getHttpStatus().value(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(e.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthErrorResponse> handleJwtException(
            Exception e,
            HttpServletRequest request
    ) {
        return ResponseEntity.internalServerError().body(new AuthErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        ));
    }
}
