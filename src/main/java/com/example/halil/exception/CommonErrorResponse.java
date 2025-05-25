package com.example.halil.exception;

import java.time.LocalDateTime;

public record CommonErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        String path
) {

}
