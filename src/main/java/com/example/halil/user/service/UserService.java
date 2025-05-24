package com.example.halil.user.service;

import com.example.halil.user.domain.Password;
import com.example.halil.user.domain.PasswordService;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.domain.UserRole;
import com.example.halil.user.dto.UserCreationDto;
import com.example.halil.user.dto.UserSignupResponseDto;
import com.example.halil.user.exception.EmailDuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;

    public UserSignupResponseDto create(UserCreationDto dto) {

        // 이메일 중복 조회
        userRepository.findFirstByEmail(dto.getEmail())
                .ifPresent(user -> {
                    throw new EmailDuplicateException();
                });

        Password encodedPassword = new Password(passwordEncoder.encode(dto.getPassword()));
        User user = new User(dto.getEmail(), encodedPassword, UserRole.ROLE_USER);

        userRepository.save(user);

        return new UserSignupResponseDto(user.getId());
    }

    public void updatePassword(long userId, String rawPassword) {
        // 유저를 찾을 수 없으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        user.updatePassword(passwordService, rawPassword);
    }
}
