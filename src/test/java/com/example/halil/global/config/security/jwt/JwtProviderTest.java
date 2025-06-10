package com.example.halil.global.config.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    @Test
    @DisplayName("액세스 토큰 생성")
    void generate_token() {
        // given
        Clock fixedGeneratedClock = Clock.fixed(Instant.parse("2022-01-01T12:00:00Z"), ZoneOffset.UTC);
        long _1SecondBeforeExpiration = TokenType.ACCESS.getExpirationSeconds() - 1; // 만료 1초 전
        Instant fixedNow = Instant.now(fixedGeneratedClock).plusSeconds(_1SecondBeforeExpiration);

        JwtClaim jwtClaim = new JwtClaim("1", "ROLE_USER", Instant.now(fixedGeneratedClock));

        AccessTokenProviderTest sut = new AccessTokenProviderTest(fixedNow);

        // when
        String result = sut.generate(jwtClaim);

        // then
        assertThat(sut.verify(result)).isTrue();
        assertThat(sut.parse(result)).isEqualTo(jwtClaim);
    }

    @Test
    @DisplayName("만료일이 지난 토큰은 유효하지 않아야 한다")
    void expired_token_is_not_valid() {
        // given
        Clock fixedGeneratedClock = Clock.fixed(Instant.parse("2030-05-07T12:00:00Z"), ZoneOffset.UTC);
        long _1SecAfterExp = TokenType.ACCESS.getExpirationSeconds() + 1; // 만료 1초 후
        Instant fixedNow = Instant.now(fixedGeneratedClock).plusSeconds(_1SecAfterExp);

        JwtClaim jwtClaim = new JwtClaim("1", "ROLE_USER", Instant.now(fixedGeneratedClock));

        AccessTokenProviderTest sut = new AccessTokenProviderTest(fixedNow);

        // when
        String result = sut.generate(jwtClaim);

        // then
        assertThat(sut.verify(result)).isFalse();
    }

    @Test
    @DisplayName("변조된 토큰은 유효히지 않아야 한다")
    void modified_token_is_not_valid() {
        // given
        Clock fixedGeneratedClock = Clock.fixed(Instant.parse("2030-05-07T12:00:00Z"), ZoneOffset.UTC);
        long _1SecBeforeExp = TokenType.ACCESS.getExpirationSeconds() - 1; // 만료 1초 전
        Instant fixedNow = Instant.now(fixedGeneratedClock).plusSeconds(_1SecBeforeExp);

        JwtClaim jwtClaim = new JwtClaim("1", "ROLE_USER", Instant.now(fixedGeneratedClock));

        String modifiedAccessToken = new AccessTokenProviderTest(
                "4a03b4ba41f806d2b6f63281cc84e64f9be980eb6497b7d68058fa6590f2d54e",
                fixedNow
        ).generate(jwtClaim);
        AccessTokenProviderTest sut = new AccessTokenProviderTest(fixedNow);

        // when
        boolean result = sut.verify(modifiedAccessToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("액세스 토큰과 리프레시 토큰은 검증 방식이 다르다")
    void different_verify_process() {
        // given
        Clock fixedGeneratedClock = Clock.fixed(Instant.parse("2030-05-07T12:00:00Z"), ZoneOffset.UTC);
        long _1SecAfterAtExp = TokenType.ACCESS.getExpirationSeconds() - 1; // 액세스 토큰 만료 1초 전
        long _1SecAfterRtExp = TokenType.REFRESH.getExpirationSeconds() - 1; // 리프레시 토큰 만료 1초 전
        Instant fixedNowForAt = Instant.now(fixedGeneratedClock).plusSeconds(_1SecAfterAtExp);
        Instant fixedNowForRt = Instant.now(fixedGeneratedClock).plusSeconds(_1SecAfterRtExp);

        JwtClaim jwtClaim = new JwtClaim("1", "ROLE_USER", Instant.now(fixedGeneratedClock));

        AccessTokenProviderTest atp = new AccessTokenProviderTest(fixedNowForAt);
        RefreshTokenProviderTest rtp = new RefreshTokenProviderTest(fixedNowForRt);

        String accessToken = atp.generate(jwtClaim);
        String refreshToken = rtp.generate(jwtClaim);

        // when
        boolean atpResult = atp.verify(refreshToken);
        boolean rtpResult = rtp.verify(accessToken);

        // then
        assertThat(atpResult).isFalse();
        assertThat(rtpResult).isFalse();
    }
}
