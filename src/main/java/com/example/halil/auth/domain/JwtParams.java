package com.example.halil.auth.domain;

public record JwtParams(
        long userId,
        String role,
        JwtType jwtType,
        Long expirationTime
) {

    public JwtParams(long userId, String role, JwtType jwtType) {
        this(userId, role, jwtType, null);
    }
}
