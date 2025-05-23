package com.example.halil.auth.service;

import com.example.halil.auth.domain.AuthTokenBundle;
import com.example.halil.auth.domain.JwtService;
import com.example.halil.auth.domain.UserInfo;
import com.example.halil.auth.domain.UserInfoService;
import com.example.halil.auth.dto.JwtBundleDto;
import com.example.halil.auth.dto.LoginRequestDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginUseCase {

    private final JwtService jwtService;
    private final UserInfoService userInfoService;

    public JwtBundleDto login(LoginRequestDto requestDto) {

        // 유저 조회
        UserInfo userInfo = userInfoService.getUserInfo(requestDto.getEmail(), requestDto.getPassword());

        // 토큰 생성
        AuthTokenBundle authTokenBundle = jwtService.generateBundle(userInfo, Instant.now());

        // DTO 매핑 & 반환
        String accessToken = authTokenBundle.accessToken();
        String refreshToken = authTokenBundle.refreshToken();

        return new JwtBundleDto(accessToken, refreshToken);
    }
}
