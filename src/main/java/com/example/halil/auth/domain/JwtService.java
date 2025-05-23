package com.example.halil.auth.domain;

import java.time.Instant;

public interface JwtService {

    AuthTokenBundle generateBundle(UserInfo userInfo, Instant issuedAt);

    AuthToken parse(String rawToken);
}
