package com.example.halil.auth.domain;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.function.UnaryOperator;
import lombok.Getter;

public enum TokenType {
    ACCESS(
            instant -> instant.plus(1L, ChronoUnit.HOURS),
            Duration.ofMinutes(30L)
    ),
    REFRESH(
            instant -> instant.atZone(ZoneId.systemDefault()).plusMonths(1L).toInstant(),
            Duration.ofDays(7L)
    );

    private final UnaryOperator<Instant> expirationTimeOperator;

    @Getter
    private final Duration threshold;

    TokenType(UnaryOperator<Instant> expirationTimeOperator, Duration threshold) {
        this.expirationTimeOperator = expirationTimeOperator;
        this.threshold = threshold;
    }

    public Instant calculateExpirationTime(Instant issuedAt) {
        return expirationTimeOperator.apply(issuedAt);
    }

}
