package com.example.halil.auth.domain;

public interface JwtService {

    String generateJwt(JwtParams jwtParams);

    boolean verifyToken(String token);

    long getUserIdFromToken(String token);

    String getUserRoleFromToken(String token);

    AuthTokenBundle generateBundleBy(UserInfo userInfo);

    AuthToken parse(String token);
}
