package com.example.halil.auth.controller;

import com.example.halil.auth.dto.AccessTokenResponseDto;
import com.example.halil.auth.dto.JwtBundleDto;
import com.example.halil.auth.dto.LoginRequestDto;
import com.example.halil.auth.dto.LoginResponseDto;
import com.example.halil.auth.service.LoginUseCase;
import com.example.halil.auth.service.TokenRefreshUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final TokenRefreshUseCase tokenRefreshUseCase;

    @PostMapping("/v1/auth/login")
    public LoginResponseDto login(
            @RequestBody @Valid LoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        JwtBundleDto jwtBundleDto = loginUseCase.login(requestDto);

        // 쿠키 세팅
        Cookie cookie = new Cookie(CookieConstants.REFRESH_TOKEN_COOKIE_NAME, jwtBundleDto.refreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute(CookieConstants.SAME_SITE_NAME, CookieConstants.SAME_SITE_VALUE);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new LoginResponseDto(jwtBundleDto.accessToken());
    }

    @PostMapping("/v1/auth/token-refresh")
    public AccessTokenResponseDto reissueToken(
            // 커스텀 예외 처리를 위해 required = false
            @CookieValue(value = CookieConstants.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        JwtBundleDto jwtBundleDto = tokenRefreshUseCase.refresh(refreshToken);
        String reissuedAccessToken = jwtBundleDto.accessToken();
        String reissuedRefreshToken = Optional.ofNullable(jwtBundleDto.refreshToken()).orElse(refreshToken);

        Cookie cookie = new Cookie(CookieConstants.REFRESH_TOKEN_COOKIE_NAME, reissuedRefreshToken);

        AccessTokenResponseDto responseDto = new AccessTokenResponseDto(reissuedAccessToken);

        if (reissuedAccessToken == null) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            return responseDto;
        }

        // refreshToken이 있으면 쿠키 재설정
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute(CookieConstants.SAME_SITE_NAME, CookieConstants.SAME_SITE_VALUE);
        response.addCookie(cookie);

        return responseDto;
    }
}
