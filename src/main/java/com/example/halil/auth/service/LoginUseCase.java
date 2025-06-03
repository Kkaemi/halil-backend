package com.example.halil.auth.service;

import com.example.halil.auth.domain.AuthToken;
import com.example.halil.auth.domain.AuthTokenFactory;
import com.example.halil.auth.domain.UserInfo;
import com.example.halil.auth.domain.UserInfoService;
import com.example.halil.auth.dto.JwtBundleDto;
import com.example.halil.auth.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginUseCase {

    private final AuthTokenFactory authTokenFactory;
    private final UserInfoService userInfoService;

    public JwtBundleDto login(LoginRequestDto requestDto) {

        // 유저 조회
        UserInfo userInfo = userInfoService.getUserInfo(requestDto.getEmail(), requestDto.getPassword());

        // 토큰 생성
        AuthToken accessToken = authTokenFactory.generateAccessToken(userInfo);
        AuthToken refreshToken = authTokenFactory.generateRefreshToken(userInfo);

        return new JwtBundleDto(accessToken, refreshToken);
    }
}
