package com.example.halil.user.controller;

import com.example.halil.user.dto.ChangePasswordRequestDto;
import com.example.halil.user.dto.UserCreationDto;
import com.example.halil.user.dto.UserSignupResponseDto;
import com.example.halil.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

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
}
