package com.example.halil.user.controller;

import com.example.halil.user.dto.ChangePasswordRequestDto;
import com.example.halil.user.dto.PasswordResetRequestDto;
import com.example.halil.user.dto.UserCreationDto;
import com.example.halil.user.dto.UserSignupResponseDto;
import com.example.halil.user.service.PasswordResetUseCase;
import com.example.halil.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final PasswordResetUseCase passwordResetUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/v1/users")
    public UserSignupResponseDto signup(@RequestBody @Valid UserCreationDto dto) {
        return userService.create(dto);
    }

    @PatchMapping("/v1/users")
    public void changePassword(
            @AuthenticationPrincipal long userId,
            @RequestBody @Valid ChangePasswordRequestDto requestDto
    ) {
        userService.updatePassword(userId, requestDto.getPassword());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/v1/users/password-reset")
    public void resetPassword(@RequestBody @Valid PasswordResetRequestDto requestDto) {
        passwordResetUseCase.resetPassword(requestDto.getEmail());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/v1/users")
    public void delete(
            @AuthenticationPrincipal long userId,
            HttpServletResponse response
    ) {
        userService.delete(userId);

        // 회원 탈퇴 후 쿠키 삭제
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
