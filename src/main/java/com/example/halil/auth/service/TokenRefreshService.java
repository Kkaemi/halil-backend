package com.example.halil.auth.service;

import com.example.halil.auth.domain.JwtParams;
import com.example.halil.auth.domain.JwtService;
import com.example.halil.auth.domain.JwtType;
import com.example.halil.auth.dto.JwtBundleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenRefreshService {

    private final JwtService jwtService;

    public JwtBundleDto refresh(String refreshToken) {
        if (!jwtService.verifyToken(refreshToken)) {
            return new JwtBundleDto(null, null);
        }

        // 리프레시 토큰의 유효기간 확인
        // 유효기간이 열흘이상 남아있으면 엑세스 토큰만 재발급
        // 유효기간이 열흘미만 남아있으면 리프레시 토큰도 함께 재발급
        // 위 기능은 추후 리팩토링하면서 구현 지금은 리프레시 토큰은 재발급하지 않음
        String accessToken = jwtService.generateJwt(new JwtParams(
                jwtService.getUserIdFromToken(refreshToken),
                jwtService.getUserRoleFromToken(refreshToken),
                JwtType.ACCESS
        ));

        return new JwtBundleDto(accessToken, null);
    }
}
