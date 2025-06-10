package com.example.halil.user.controller;

import com.example.halil.global.config.security.jwt.JwtClaim;
import com.example.halil.global.config.security.jwt.JwtProviderFactory;
import com.example.halil.user.dto.JwtBundleDto;
import com.example.halil.user.dto.LoginRequestDto;
import com.example.halil.user.dto.LoginResponseDto;
import com.example.halil.user.service.LoginUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private static final String COOKIE_NAME = "refresh_token";
    private static final String SAME_SITE = "SameSite";
    private static final String SAME_SITE_VALUE = "Strict";

    private final LoginUseCase loginUseCase;
    private final JwtProviderFactory jwtProviderFactory;

    @PostMapping("/v1/users/authentication")
    public LoginResponseDto login(
            @RequestBody @Valid LoginRequestDto dto,
            HttpServletResponse response
    ) {
        JwtBundleDto jwtBundleDto = loginUseCase.login(dto);

        // 쿠키 세팅
        Cookie cookie = new Cookie(COOKIE_NAME, jwtBundleDto.refreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute(SAME_SITE, SAME_SITE_VALUE);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new LoginResponseDto(jwtBundleDto.accessToken());
    }

    @PostMapping("/v1/users/tokens")
    public LoginResponseDto reissueToken(
            @CookieValue(value = COOKIE_NAME) String refreshToken,
            HttpServletResponse response
    ) {
        boolean verified = jwtProviderFactory.refreshToken().verify(refreshToken);
        Cookie cookie = new Cookie(COOKIE_NAME, null);

        if (!verified) {
            // 리프레시 토큰이 유효하지 않으면 쿠키 삭제
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            throw new RuntimeException();
        }

        // 토큰 재발급
        JwtClaim jwtClaim = jwtProviderFactory.refreshToken().parse(refreshToken);
        String accessToken = jwtProviderFactory.accessToken().generate(jwtClaim);
        String newRefreshToken = jwtProviderFactory.refreshToken().generate(jwtClaim);

        // 쿠키 재설정
        cookie.setValue(newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute(SAME_SITE, SAME_SITE_VALUE);
        response.addCookie(cookie);

        return new LoginResponseDto(accessToken);
    }
}
