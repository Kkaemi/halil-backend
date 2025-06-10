package com.example.halil.global.config.security.jwt;

import com.example.halil.global.config.properties.JwtProperties;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class AccessTokenProvider extends AbstractJwtProvider {

    private final JwtProperties jwtProperties;

    @Override
    protected TokenType getType() {
        return TokenType.ACCESS;
    }

    @Override
    protected String getSecret() {
        return jwtProperties.getAccessTokenSecret();
    }

    @Override
    protected Instant getNow() {
        return Instant.now();
    }
}
