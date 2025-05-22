package com.example.halil.auth.infra;

import com.example.halil.auth.domain.UserInfo;
import com.example.halil.auth.domain.UserInfoService;
import com.example.halil.auth.exception.AuthErrorCode;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class UserInfoServiceImpl implements UserInfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserInfo getUserInfo(String email, String password) {
        // 유저를 찾을 수 없으면 예외 발생
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(AuthErrorCode.USER_NOT_FOUND_BY_EMAIL::asException);

        // 비밀번호 일치 검사
        if (!passwordEncoder.matches(password, user.getPassword().getValue())) {
            throw AuthErrorCode.PASSWORD_MISMATCH.asException();
        }

        return new UserInfo(user.getId(), user.getRole().name());
    }
}
