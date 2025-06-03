package com.example.halil.auth.service;

import com.example.halil.auth.domain.AuthToken;
import com.example.halil.auth.domain.AuthTokenFactory;
import com.example.halil.auth.dto.JwtBundleDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenRefreshUseCase {

    private final AuthTokenFactory authTokenFactory;

    public JwtBundleDto refresh(String rawToken) {
        AuthToken refreshToken = authTokenFactory.parseRefreshToken(rawToken);

        // 리프레시 토큰이 유효하지 않으면 로그아웃 처리
        if (!refreshToken.isValidFromNow(Instant.now())) {
            return new JwtBundleDto(null, null);
        }

        // 엑세스 토큰 재발급
        AuthToken accessToken = authTokenFactory.generateAccessToken(refreshToken.getUserInfo());

        // 리프레시 토큰이 만료일에 가까우면 리프레시 토큰도 재발급
        if (refreshToken.isCloseExpirationTimeFromNow(Instant.now())) {
            AuthToken newRefreshToken = authTokenFactory.generateRefreshToken(refreshToken.getUserInfo());
            return new JwtBundleDto(accessToken, newRefreshToken);
        }

        return new JwtBundleDto(accessToken, refreshToken);
    }
}
