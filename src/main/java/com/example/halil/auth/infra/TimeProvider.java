package com.example.halil.auth.infra;

import java.time.Instant;

public interface TimeProvider {

    Instant now();
}
