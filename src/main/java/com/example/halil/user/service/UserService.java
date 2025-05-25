package com.example.halil.user.service;

import com.example.halil.user.domain.PasswordService;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.domain.UserRole;
import com.example.halil.user.domain.exception.PasswordCannotBeReused;
import com.example.halil.user.dto.UserCreationDto;
import com.example.halil.user.dto.UserSignupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserSignupResponseDto create(UserCreationDto dto) {

        // 이메일 중복 조회
        userRepository.findFirstByEmail(dto.getEmail())
                .ifPresent(user -> {
                    throw UserErrorCode.EMAIL_ALREADY_EXISTS.exception();
                });

        String encodedPassword = passwordService.encode(dto.getPassword());
        User user = new User(dto.getEmail(), encodedPassword, UserRole.ROLE_USER);

        userRepository.save(user);

        return new UserSignupResponseDto(user.getId());
    }

    public void updatePassword(long userId, String rawPassword) {
        // 유저를 찾을 수 없으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(UserErrorCode.USER_NOT_FOUND_BY_EMAIL::exception);

        try {
            user.updatePassword(passwordService, rawPassword);
        } catch (PasswordCannotBeReused e) {
            throw UserErrorCode.PASSWORD_CANNOT_BE_REUSED.exception();
        }
    }
}
