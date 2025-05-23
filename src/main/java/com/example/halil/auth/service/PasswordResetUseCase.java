package com.example.halil.auth.service;

import com.example.halil.auth.domain.MailService;
import com.example.halil.auth.domain.TemporaryPasswordGenerator;
import com.example.halil.auth.domain.UserService;
import com.example.halil.auth.dto.PasswordResetRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PasswordResetUseCase {

    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final UserService userService;
    private final MailService mailService;

    public void resetPassword(PasswordResetRequestDto requestDto) {
        // 임의 비밀번호 생성
        String temporaryPassword = temporaryPasswordGenerator.generate();

        // 요청받은 이메일로 유저 조회
        // 유저 비밀번호 업데이트
        userService.setTemporaryPassword(requestDto.getEmail(), temporaryPassword);

        // 변경된 비밀번호를 비동기로 메일 전송
        mailService.sendPasswordResetMail(requestDto.getEmail(), temporaryPassword);
    }
}
