package com.example.halil.auth.domain;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.function.UnaryOperator;

public enum TokenType {
    ACCESS(instant -> instant.plus(1L, ChronoUnit.HOURS)),
    REFRESH(instant -> instant.atZone(ZoneId.systemDefault()).plusMonths(1L).toInstant());

    private final UnaryOperator<Instant> expirationTimeOperator;

    TokenType(UnaryOperator<Instant> expirationTimeOperator) {
        this.expirationTimeOperator = expirationTimeOperator;
    }

    public Instant calculateExpirationTime(Instant issuedAt) {
        return expirationTimeOperator.apply(issuedAt);
    }
}
