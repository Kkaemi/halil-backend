package com.example.halil.auth.domain;

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

    private final long userId;

    private final String role;

    @Getter(AccessLevel.NONE)
    private final boolean verified;

    public boolean isValid() {
        return this.verified;
    }
}
