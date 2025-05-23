package com.example.halil.auth.service;

import com.example.halil.auth.domain.AuthToken;
import com.example.halil.auth.domain.AuthTokenBundle;
import com.example.halil.auth.domain.JwtService;
import com.example.halil.auth.dto.JwtBundleDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenRefreshService {

    private final JwtService jwtService;

    public JwtBundleDto refresh(String rawToken) {
        AuthToken refreshToken = jwtService.parse(rawToken);

        // 리프레시 토큰이 유효하지 않으면 로그아웃 처리
        if (!refreshToken.isValid()) {
            return new JwtBundleDto(null, null);
        }

        // 토큰 재발급
        AuthTokenBundle authTokenBundle = jwtService.generateBundle(refreshToken.getUserInfo(), Instant.now());

        // 리프레시 토큰이 만료일에 가까우면 리프레시 토큰도 재발급
        if (refreshToken.isCloseExpirationTime()) {
            return new JwtBundleDto(authTokenBundle.accessToken(), authTokenBundle.refreshToken());
        }

        return new JwtBundleDto(authTokenBundle.accessToken(), null);
    }
}
