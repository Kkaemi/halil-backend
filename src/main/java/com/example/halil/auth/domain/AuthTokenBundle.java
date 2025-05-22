package com.example.halil.auth.domain;

public record AuthTokenBundle(
        String accessToken,
        String refreshToken
) {

}
