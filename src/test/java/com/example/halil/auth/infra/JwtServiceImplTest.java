package com.example.halil.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.halil.auth.domain.AuthToken;
import com.example.halil.auth.domain.AuthTokenBundle;
import com.example.halil.auth.domain.TokenType;
import com.example.halil.auth.domain.UserInfo;
import com.example.halil.properties.JwtProperties;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZonedDateTime;
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
    @DisplayName("토큰 번들 생성")
    void generateBundle() {
        // given
        long userId = 1L;
        String role = "ROLE_USER";
        UserInfo userInfo = new UserInfo(userId, role);
        Instant now = ZonedDateTime.now().toInstant();

        // when
        AuthTokenBundle authTokenBundle = jwtServiceImpl.generateBundle(userInfo, now);

        // then
        assertThat(authTokenBundle.accessToken()).isNotNull();
        assertThat(authTokenBundle.refreshToken()).isNotNull();
    }

    @Test
    @DisplayName("토큰 파싱")
    void parse() {
        // given
        long userId = 1L;
        String role = "ROLE_USER";
        UserInfo userInfo = new UserInfo(userId, role);
        Instant now = ZonedDateTime.now().toInstant();
        AuthTokenBundle authTokenBundle = jwtServiceImpl.generateBundle(userInfo, now);

        // when
        AuthToken authToken = jwtServiceImpl.parse(authTokenBundle.accessToken());

        // then
        assertThat(authToken.getValue()).isNotNull();
        assertThat(authToken.getType()).isEqualTo(TokenType.ACCESS);
        assertThat(authToken.getIssuedAt().value()).isNotNull();
        assertThat(authToken.getExpirationTime().value()).isNotNull();
        assertThat(authToken.getIssuedAt().value().isBefore(authToken.getExpirationTime().value())).isTrue();
        assertThat(authToken.getUserInfo()).isEqualTo(userInfo);
        assertThat(authToken.isValid()).isTrue();
    }
}
