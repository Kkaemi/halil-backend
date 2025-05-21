package com.example.halil.auth;

public record JwtGenerationDto(long userId, String role, JwtType jwtType) {

}
