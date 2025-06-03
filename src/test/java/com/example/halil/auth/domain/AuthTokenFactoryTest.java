package com.example.halil.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.halil.auth.infra.AuthTokenFactoryImpl;
import com.example.halil.auth.infra.FixedTimeProvider;
import com.example.halil.auth.infra.TimeProvider;
import com.example.halil.properties.JwtProperties;
import java.security.SecureRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthTokenFactoryTest {

    @Test
    @DisplayName("토큰 생성")
    void generate_access_toke() {
        // given
        UserInfo userInfo = new UserInfo(1L, "ROLE_USER");

        JwtProperties jwtProperties = createJwtProperties();
        TimeProvider timeProvider = FixedTimeProvider.of(2022, 1, 1, 12, 0, 0);

        AuthTokenFactoryImpl sut = new AuthTokenFactoryImpl(jwtProperties, timeProvider);

        // when
        AuthToken accessToken = sut.generateAccessToken(userInfo);
        AuthToken refreshToken = sut.generateRefreshToken(userInfo);

        // then
        assertThat(accessToken.isValidFromNow(timeProvider.now())).isTrue();
        assertThat(accessToken.getType()).isEqualTo(TokenType.ACCESS);
        assertThat(refreshToken.isValidFromNow(timeProvider.now())).isTrue();
        assertThat(refreshToken.getType()).isEqualTo(TokenType.REFRESH);
    }

    @Test
    @DisplayName("만료일이 지난 토큰은 유효하지 않아야 한다")
    void after_expiration_time_token_is_not_valid() {
        // given
        UserInfo userInfo = new UserInfo(1L, "ROLE_USER");

        JwtProperties jwtProperties = createJwtProperties();

        TimeProvider createdTime = FixedTimeProvider.of(
                2022, 1, 1, 12, 0, 0
        );
        TimeProvider afterExpirationTime = FixedTimeProvider.of(
                2023, 1, 1, 12, 0, 0
        );

        AuthTokenFactoryImpl sut = new AuthTokenFactoryImpl(jwtProperties, createdTime);

        // when
        AuthToken accessToken = sut.generateAccessToken(userInfo);
        AuthToken refreshToken = sut.generateRefreshToken(userInfo);

        // then
        assertThat(accessToken.isValidFromNow(afterExpirationTime.now())).isFalse();
        assertThat(accessToken.getType()).isEqualTo(TokenType.ACCESS);
        assertThat(refreshToken.isValidFromNow(afterExpirationTime.now())).isFalse();
        assertThat(refreshToken.getType()).isEqualTo(TokenType.REFRESH);
    }

    @Test
    @DisplayName("변조된 토큰은 유효히지 않아야 한다")
    void modified_token_is_not_valid() {
        // given
        JwtProperties jwtProperties = createJwtProperties();
        JwtProperties otherJwtProperties = createJwtProperties();

        UserInfo userInfo = new UserInfo(1L, "ROLE_USER");

        TimeProvider timeProvider = FixedTimeProvider.of(2023, 1, 1, 12, 0, 0);

        AuthTokenFactory factory = new AuthTokenFactoryImpl(jwtProperties, timeProvider);
        AuthTokenFactory otherFactory = new AuthTokenFactoryImpl(otherJwtProperties, timeProvider);

        AuthToken otherToken = otherFactory.generateAccessToken(userInfo);
        AuthToken sut = factory.parseAccessToken(otherToken.getValue());

        // when
        boolean result = sut.isValidFromNow(timeProvider.now());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("토큰은 만료일에 가까운지 스스로 검증 가능해야 한다")
    void close_from_expired_time() {
        // given
        JwtProperties jwtProperties = createJwtProperties();

        UserInfo userInfo = new UserInfo(1L, "ROLE_USER");

        TimeProvider createdTime = FixedTimeProvider.of(
                2023, 1, 1, 12, 0, 0
        );
        TimeProvider accessTokenThreshold = FixedTimeProvider.of(
                2023, 1, 1, 12, 40, 0
        );
        TimeProvider refreshTokenThreshold = FixedTimeProvider.of(
                2023, 1, 28, 12, 0, 0
        );

        AuthTokenFactory factory = new AuthTokenFactoryImpl(jwtProperties, createdTime);
        AuthToken accessToken = factory.generateAccessToken(userInfo);
        AuthToken refreshToken = factory.generateRefreshToken(userInfo);

        // when
        boolean result1 = accessToken.isCloseExpirationTimeFromNow(accessTokenThreshold.now());
        boolean result2 = refreshToken.isCloseExpirationTimeFromNow(refreshTokenThreshold.now());

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
    }

    private JwtProperties createJwtProperties() {
        byte[] bytes = new byte[32];
        SecureRandom secureRandom = new SecureRandom();

        secureRandom.nextBytes(bytes);
        String accessTokenSecret = new String(bytes);

        secureRandom.nextBytes(bytes);
        String refreshTokenSecret = new String(bytes);

        return new JwtProperties(accessTokenSecret, refreshTokenSecret);
    }
}
