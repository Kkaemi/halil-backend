package com.example.halil.auth.infra;

import com.example.halil.auth.domain.UserInfo;
import com.example.halil.auth.domain.UserInfoService;
import com.example.halil.auth.service.AuthErrorCode;
import com.example.halil.user.domain.PasswordFactory;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Component
public class UserInfoServiceImpl implements UserInfoService {

    private final PasswordFactory passwordFactory;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserInfo getUserInfo(String email, String rawPassword) {
        // 유저를 찾을 수 없으면 예외 발생
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(AuthErrorCode.USER_NOT_FOUND_BY_EMAIL::exception);

        user.authenticateWith(passwordFactory.createPassword(rawPassword));

        return new UserInfo(user.getId(), user.getRole().name());
    }
}
