package com.example.halil.auth.exception;

import java.time.LocalDateTime;

public record AuthErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        String path
) {

}
