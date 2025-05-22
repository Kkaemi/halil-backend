package com.example.halil.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.halil.auth.domain.JwtParams;
import com.example.halil.auth.domain.JwtType;
import com.example.halil.properties.JwtProperties;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtServiceImplTest {

    private static JwtServiceImpl jwtServiceImpl;

    @BeforeAll
    public static void setUp() {
        byte[] bytes = new byte[32];
        SecureRandom random = new SecureRandom();

        random.nextBytes(bytes);
        String accessTokenSecret = new String(bytes);

        random.nextBytes(bytes);
        String refreshTokenSecret = new String(bytes);

        JwtProperties jwtProperties = new JwtProperties(accessTokenSecret, refreshTokenSecret);
        jwtServiceImpl = new JwtServiceImpl(jwtProperties);
    }

    @Test
    @DisplayName("토큰 생성")
    void generateAccessToken() {
        // given
        long userId = 1L;
        String role = "ROLE_USER";
        JwtParams accessTokenDto = new JwtParams(userId, role, JwtType.ACCESS);
        JwtParams refreshTokenDto = new JwtParams(userId, role, JwtType.REFRESH);

        // when
        String accessToken = jwtServiceImpl.generateJwt(accessTokenDto);
        String refreshToken = jwtServiceImpl.generateJwt(refreshTokenDto);
        boolean isAccessTokenVerified = jwtServiceImpl.verifyToken(accessToken);
        boolean isRefreshTokenVerified = jwtServiceImpl.verifyToken(refreshToken);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(refreshToken).isNotNull();
        assertThat(isAccessTokenVerified).isTrue();
        assertThat(isRefreshTokenVerified).isTrue();
        assertThat(jwtServiceImpl.getUserIdFromToken(accessToken)).isEqualTo(userId);
        assertThat(jwtServiceImpl.getUserIdFromToken(refreshToken)).isEqualTo(userId);
        assertThat(jwtServiceImpl.getUserRoleFromToken(accessToken)).isEqualTo(role);
        assertThat(jwtServiceImpl.getUserRoleFromToken(refreshToken)).isEqualTo(role);
    }

    @Test
    @DisplayName("외부에서 만들어진 토큰은 검증 결과 false")
    void verifyToken() {
        // given
        long userId = 1L;
        String role = "ROLE_USER";
        JwtParams dto = new JwtParams(userId, role, JwtType.ACCESS);

        byte[] bytes = new byte[32];
        SecureRandom random = new SecureRandom();

        random.nextBytes(bytes);
        String accessTokenSecret = new String(bytes);

        random.nextBytes(bytes);
        String refreshTokenSecret = new String(bytes);

        JwtProperties jwtProperties = new JwtProperties(accessTokenSecret, refreshTokenSecret);
        JwtServiceImpl otherService = new JwtServiceImpl(jwtProperties);
        String modifiedAccessToken = otherService.generateJwt(dto);

        // when
        boolean isTokenValid = jwtServiceImpl.verifyToken(modifiedAccessToken);

        // then
        assertThat(isTokenValid).isFalse();
    }
}
