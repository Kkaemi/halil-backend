package com.example.halil.auth;

import lombok.Getter;

@Getter
public enum JwtType {

    ACCESS(1000L * 60 * 60),
    REFRESH(1000L * 60 * 60 * 24 * 30);

    private final long expirationTime;

    JwtType(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
