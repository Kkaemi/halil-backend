package com.example.halil.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.halil.properties.JwtProperties;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static JwtService jwtService;

    @BeforeAll
    public static void setUp() {
        byte[] bytes = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        JwtProperties jwtProperties = new JwtProperties(new String(bytes));
        jwtService = new JwtService(jwtProperties);
    }

    @Test
    @DisplayName("토큰 생성")
    void generateAccessToken() {
        // given
        long userId = 1L;

        // when
        String accessToken = jwtService.generateAccessToken(userId);
        String refreshToken = jwtService.generateRefreshToken(userId);
        boolean isAccessTokenVerified = jwtService.verifyToken(accessToken);
        boolean isRefreshTokenVerified = jwtService.verifyToken(refreshToken);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(refreshToken).isNotNull();
        assertThat(isAccessTokenVerified).isTrue();
        assertThat(isRefreshTokenVerified).isTrue();
        assertThat(jwtService.getUserIdFromToken(accessToken)).isEqualTo(userId);
        assertThat(jwtService.getUserIdFromToken(refreshToken)).isEqualTo(userId);
    }

    @Test
    @DisplayName("외부에서 만들어진 토큰은 검증 결과 false")
    void verifyToken() {
        // given
        long userId = 1L;

        byte[] bytes = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);

        JwtProperties jwtProperties = new JwtProperties(new String(bytes));
        JwtService otherService = new JwtService(jwtProperties);
        String modifiedAccessToken = otherService.generateAccessToken(userId);

        // when
        boolean isTokenValid = jwtService.verifyToken(modifiedAccessToken);

        // then
        assertThat(isTokenValid).isFalse();
    }
}
