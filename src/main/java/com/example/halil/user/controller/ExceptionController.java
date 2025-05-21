package com.example.halil.user.controller;

import com.example.halil.user.exception.EmailDuplicateException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = {UserController.class})
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({EmailDuplicateException.class})
    public ResponseEntity<String> handleEmailDuplicateException(EmailDuplicateException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(
                String.format("""
                        {
                            "message": "%s"
                        }
                        """, e.getMessage())
        );
    }
}
