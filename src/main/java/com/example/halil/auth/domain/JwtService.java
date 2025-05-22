package com.example.halil.auth.domain;

import java.util.Date;

public interface JwtService {

    String generateJwt(JwtParams jwtParams);

    boolean verifyToken(String token);

    long getUserIdFromToken(String token);

    String getUserRoleFromToken(String token);

    Date getExpirationTimeFromToken(String token);
}
