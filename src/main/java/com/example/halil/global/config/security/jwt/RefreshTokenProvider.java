package com.example.halil.global.config.security.jwt;

import com.example.halil.global.config.properties.JwtProperties;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class RefreshTokenProvider extends AbstractJwtProvider {

    private final JwtProperties jwtProperties;

    @Override
    protected TokenType getType() {
        return TokenType.REFRESH;
    }

    @Override
    protected String getSecret() {
        return jwtProperties.getRefreshTokenSecret();
    }

    @Override
    protected Instant getNow() {
        return Instant.now();
    }
}
