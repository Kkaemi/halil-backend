package com.example.halil.auth.dto;

public record JwtBundleDto(
        String accessToken,
        String refreshToken
) {

}
