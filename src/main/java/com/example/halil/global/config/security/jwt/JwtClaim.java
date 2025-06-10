package com.example.halil.global.config.security.jwt;

import java.time.Instant;

public record JwtClaim(
        String subject,
        String role,
        Instant issuedAt
) {

}
