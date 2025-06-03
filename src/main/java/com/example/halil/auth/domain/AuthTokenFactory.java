package com.example.halil.auth.domain;

public interface AuthTokenFactory {

    AuthToken generateAccessToken(UserInfo userInfo);

    AuthToken parseAccessToken(String rawToken);

    AuthToken generateRefreshToken(UserInfo userInfo);

    AuthToken parseRefreshToken(String rawToken);
}
