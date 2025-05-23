package com.example.halil.auth.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AuthToken {

    private final String value;

    private final TokenType type;

    private final IssuedAt issuedAt;

    private final ExpirationTime expirationTime;

    private final UserInfo userInfo;

    @Getter(AccessLevel.NONE)
    private final boolean verified;

    public boolean isValid() {
        return this.verified;
    }

    public boolean isCloseExpirationTime() {
        return Instant.now().isAfter(expirationTime.value().minus(7L, ChronoUnit.DAYS));
    }
}
