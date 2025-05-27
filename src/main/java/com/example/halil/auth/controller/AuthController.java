package com.example.halil.auth.controller;

import com.example.halil.auth.dto.AccessTokenResponseDto;
import com.example.halil.auth.dto.JwtBundleDto;
import com.example.halil.auth.dto.LoginRequestDto;
import com.example.halil.auth.dto.LoginResponseDto;
import com.example.halil.auth.dto.PasswordResetRequestDto;
import com.example.halil.auth.service.LoginUseCase;
import com.example.halil.auth.service.PasswordResetUseCase;
import com.example.halil.auth.service.TokenRefreshUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final TokenRefreshUseCase tokenRefreshUseCase;
    private final PasswordResetUseCase passwordResetUseCase;

    @PostMapping("/v1/auth/login")
    public LoginResponseDto login(
            @RequestBody @Valid LoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        JwtBundleDto jwtBundleDto = loginUseCase.login(requestDto);

        // 쿠키 세팅
        Cookie cookie = new Cookie("refresh_token", jwtBundleDto.refreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setPath("/");
        response.addCookie(cookie);

        return new LoginResponseDto(jwtBundleDto.accessToken());
    }

    @PostMapping("/v1/auth/token-refresh")
    public AccessTokenResponseDto reissueToken(
            // 커스텀 예외 처리를 위해 required = false
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        JwtBundleDto jwtBundleDto = tokenRefreshUseCase.refresh(refreshToken);
        String reissuedAccessToken = jwtBundleDto.accessToken();
        String reissuedRefreshToken = Optional.ofNullable(jwtBundleDto.refreshToken()).orElse(refreshToken);

        Cookie cookie = new Cookie("refresh_token", reissuedRefreshToken);

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
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);

        return responseDto;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/v1/auth/password-reset")
    public void resetPassword(@RequestBody @Valid PasswordResetRequestDto requestDto) {
        passwordResetUseCase.resetPassword(requestDto);
    }
}
