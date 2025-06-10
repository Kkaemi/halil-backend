package com.example.halil.global.config.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum TokenType {
    ACCESS(86400L), REFRESH(2592000L);

    private final long expirationSeconds;
}
