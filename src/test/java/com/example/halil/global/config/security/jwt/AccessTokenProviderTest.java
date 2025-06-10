package com.example.halil.global.config.security.jwt;

import java.time.Instant;

public class AccessTokenProviderTest extends AbstractJwtProvider {

    private final String secret;
    private final Instant now;

    public AccessTokenProviderTest() {
        this.secret = null;
        this.now = null;
    }

    public AccessTokenProviderTest(Instant now) {
        this.secret = null;
        this.now = now;
    }

    public AccessTokenProviderTest(String secret, Instant now) {
        this.secret = secret;
        this.now = now;
    }

    @Override
    protected TokenType getType() {
        return TokenType.ACCESS;
    }

    @Override
    protected String getSecret() {
        return this.secret == null
                ? "874429e60b450ee2225e598ea9c10d323a60b98cba3c40c4d8a8c27394fecdfa"
                : secret;
    }

    @Override
    protected Instant getNow() {
        return now == null ? Instant.now() : now;
    }
}
