package com.example.halil.auth.infra;

import com.example.halil.auth.domain.UserInfo;
import com.example.halil.auth.domain.UserService;
import com.example.halil.auth.service.AuthErrorCode;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public UserInfo getUserInfo(String email, String rawPassword) {
        // 유저를 찾을 수 없으면 예외 발생
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(AuthErrorCode.USER_NOT_FOUND_BY_EMAIL::exception);

        // 비밀번호 일치 검사
        if (!passwordEncoder.matches(rawPassword, user.getEncodedPassword())) {
            throw AuthErrorCode.PASSWORD_MISMATCH.exception();
        }

        return new UserInfo(user.getId(), user.getRole().name());
    }

    @Override
    public void setTemporaryPassword(String email, String temporaryPassword) {
        // 유저를 찾을 수 없으면 예외 발생
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(AuthErrorCode.USER_NOT_FOUND_BY_EMAIL::exception);

        // 임시 비밀번호 설정
        user.setTemporarilyPassword(passwordEncoder.encode(temporaryPassword));
    }
}
