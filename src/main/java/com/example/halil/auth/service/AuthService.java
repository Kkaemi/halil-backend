package com.example.halil.auth.service;

import com.example.halil.auth.domain.AuthErrorCode;
import com.example.halil.auth.domain.JwtParams;
import com.example.halil.auth.domain.JwtService;
import com.example.halil.auth.domain.JwtType;
import com.example.halil.auth.domain.UserInfo;
import com.example.halil.auth.domain.UserInfoService;
import com.example.halil.auth.dto.JwtBundleDto;
import com.example.halil.auth.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserInfoService userInfoService;
    private final PasswordEncoder passwordEncoder;

    public JwtBundleDto login(LoginRequestDto requestDto) {

        // 이메일로 유저 조회
        UserInfo userInfo = userInfoService.getUserInfo(requestDto.getEmail());

        // 비밀번호 일치 검사
        if (!passwordEncoder.matches(requestDto.getPassword(), userInfo.password())) {
            throw AuthErrorCode.PASSWORD_MISMATCH.asException();
        }

        JwtParams accessTokenGenerationDto = new JwtParams(
                userInfo.userId(), userInfo.role(), JwtType.ACCESS
        );
        JwtParams refreshTokenGenerationDto = new JwtParams(
                userInfo.userId(), userInfo.role(), JwtType.REFRESH
        );
        String accessToken = jwtService.generateJwt(accessTokenGenerationDto);
        String refreshToken = jwtService.generateJwt(refreshTokenGenerationDto);

        return new JwtBundleDto(accessToken, refreshToken);
    }
}
