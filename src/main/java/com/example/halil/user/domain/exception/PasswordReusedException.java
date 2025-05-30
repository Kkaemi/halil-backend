package com.example.halil.user.domain.exception;

public class PasswordReusedException extends RuntimeException {

    public PasswordReusedException(String message) {
        super(message);
    }
}
