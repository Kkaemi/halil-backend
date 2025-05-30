package com.example.halil.user.domain.exception;

public class EmailDuplicateException extends RuntimeException {

    public EmailDuplicateException(String message) {
        super(message);
    }
}
