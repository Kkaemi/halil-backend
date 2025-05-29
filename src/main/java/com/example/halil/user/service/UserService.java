package com.example.halil.user.service;

import com.example.halil.user.domain.Password;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.domain.UserRole;
import com.example.halil.user.domain.UserStatus;
import com.example.halil.user.domain.exception.PasswordCannotBeReused;
import com.example.halil.user.dto.UserCreationDto;
import com.example.halil.user.dto.UserSignupResponseDto;
import com.example.halil.user.infra.BCryptPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserSignupResponseDto create(UserCreationDto dto) {

        // 이메일 중복 조회
        userRepository.findFirstByEmail(dto.getEmail())
                .ifPresent(user -> {
                    throw UserErrorCode.EMAIL_ALREADY_EXISTS.exception();
                });

        Password password = new BCryptPassword(dto.getPassword());
        User user = new User(dto.getEmail(), password, UserRole.ROLE_USER, UserStatus.ACTIVE);

        userRepository.save(user);

        return new UserSignupResponseDto(user.getId());
    }

    public void updatePassword(long userId, String rawPassword) {
        // 유저를 찾을 수 없으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(UserErrorCode.USER_NOT_FOUND_BY_ID::exception);

        Password password = new BCryptPassword(rawPassword);

        try {
            user.updatePassword(password);
        } catch (PasswordCannotBeReused e) {
            throw UserErrorCode.PASSWORD_CANNOT_BE_REUSED.exception();
        }
    }

    public void delete(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserErrorCode.USER_NOT_FOUND_BY_ID::exception);

        user.withdraw();
    }
}
