package com.example.halil.auth.dto;

import com.example.halil.auth.domain.AuthToken;

public record JwtBundleDto(
        AuthToken accessToken,
        AuthToken refreshToken
) {

}
