package com.example.halil.user.service;

import com.example.halil.user.domain.Password;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.domain.exception.EmailDuplicateException;
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
                    throw new EmailDuplicateException("이미 존재하는 이메일 입니다.");
                });

        Password password = new BCryptPassword(dto.getPassword());
        User user = new User(dto.getEmail(), password);

        userRepository.save(user);

        return new UserSignupResponseDto(user.getId());
    }

    public void updatePassword(long userId, String rawPassword) {
        // 유저를 찾을 수 없으면 예외 발생
        User user = userRepository.findById(userId).orElseThrow();

        Password password = new BCryptPassword(rawPassword);

        user.updatePassword(password);
    }

    public void delete(long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        user.withdraw();
    }
}
