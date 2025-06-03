package com.example.halil.auth.infra;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FixedTimeProvider implements TimeProvider {

    private final Instant currentTime;

    public FixedTimeProvider(Instant currentTime) {
        this.currentTime = currentTime;
    }

    public static TimeProvider of(int year, int month, int day, int hour, int minute, int second) {
        return new FixedTimeProvider(
                LocalDateTime.of(year, month, day, hour, minute, second)
                        .toInstant(ZoneOffset.UTC)
        );
    }

    @Override
    public Instant now() {
        return currentTime;
    }
}
