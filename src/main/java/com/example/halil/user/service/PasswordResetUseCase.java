package com.example.halil.user.service;

import com.example.halil.user.domain.TemporaryPasswordGenerator;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.infra.BCryptPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PasswordResetUseCase {

    private final UserRepository userRepository;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final MailService mailService;

    public void resetPassword(String email) {
        // 임의 비밀번호 생성
        String temporaryPassword = temporaryPasswordGenerator.generate();

        // 요청받은 이메일로 유저 조회
        // 유저 비밀번호 업데이트
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow();
        user.setTemporaryPassword(new BCryptPassword(temporaryPassword));

        // 변경된 비밀번호를 비동기로 메일 전송
        mailService.sendPasswordResetMail(email, temporaryPassword);
    }
}
