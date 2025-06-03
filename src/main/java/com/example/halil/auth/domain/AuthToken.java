package com.example.halil.auth.domain;

import java.time.Instant;
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

    public boolean isValidFromNow(Instant now) {
        return this.verified && now.isBefore(expirationTime.value());
    }

    public boolean isCloseExpirationTimeFromNow(Instant now) {
        return now.isAfter(expirationTime.value().minus(this.type.getThreshold()))
                && now.isBefore(expirationTime.value());
    }
}
