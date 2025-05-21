package com.example.halil.auth.infra;

import com.example.halil.auth.domain.AuthErrorCode;
import com.example.halil.auth.domain.UserInfo;
import com.example.halil.auth.domain.UserInfoService;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class UserInfoServiceImpl implements UserInfoService {

    private final UserRepository userRepository;

    @Override
    public UserInfo getUserInfo(String email) {
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(AuthErrorCode.USER_NOT_FOUND_BY_EMAIL::asException);

        return new UserInfo(
                user.getId(),
                user.getPassword().getValue(),
                user.getRole().name()
        );
    }
}
