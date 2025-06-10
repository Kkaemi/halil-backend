package com.example.halil.global.config.security.jwt;

import java.time.Instant;

public class RefreshTokenProviderTest extends AbstractJwtProvider {

    private final String secret;
    private final Instant now;

    public RefreshTokenProviderTest() {
        this.secret = null;
        this.now = null;
    }

    public RefreshTokenProviderTest(Instant now) {
        this.secret = null;
        this.now = now;
    }

    public RefreshTokenProviderTest(String secret, Instant now) {
        this.secret = secret;
        this.now = now;
    }

    @Override
    protected TokenType getType() {
        return TokenType.REFRESH;
    }

    @Override
    protected String getSecret() {
        return this.secret == null
                ? "6a8663d27e711d27dfbe6162e58a0fe7446018c02fe43a13a49a72dd7d7903f4"
                : secret;
    }

    @Override
    protected Instant getNow() {
        return now == null ? Instant.now() : now;
    }
}
