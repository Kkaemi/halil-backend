package com.example.halil.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.halil.user.domain.Password;
import com.example.halil.user.domain.PasswordFactory;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.domain.exception.EmailDuplicateException;
import com.example.halil.user.domain.exception.PasswordReusedException;
import com.example.halil.user.dto.UserCreationDto;
import com.example.halil.user.dto.UserSignupResponseDto;
import com.example.halil.user.infra.PasswordFactoryImpl;
import com.example.halil.user.infra.UserMemoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserInfoServiceTest {

    private final UserRepository userRepository;
    private final PasswordFactory passwordFactory;

    public UserInfoServiceTest() {
        userRepository = new UserMemoryRepository();
        passwordFactory = new PasswordFactoryImpl();
    }

    @AfterEach
    void after_each() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입")
    void register_user() {
        // given
        long userId = 1L;
        String email = "thisisemail@email.com";
        String rawPassword = "this_is_password1234";
        UserCreationDto userCreationDto = new UserCreationDto(email, rawPassword);
        UserService sut = new UserService(userRepository, passwordFactory);

        // when
        UserSignupResponseDto result = sut.create(userCreationDto);

        // then
        assertThat(result.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("중복된 이메일은 회원가입 불가")
    void duplicate_email_cannot_register() {
        // given
        String email = "thisisemail@email.com";
        String rawPassword = "this_is_password!1";
        Password password = passwordFactory.createPassword(rawPassword);
        User dummyUser = new User(email, password);

        userRepository.save(dummyUser);

        UserCreationDto userCreationDto = new UserCreationDto(email, rawPassword);

        UserService sut = new UserService(userRepository, passwordFactory);

        // when and then
        assertThatThrownBy(() -> sut.create(userCreationDto))
                .isInstanceOf(EmailDuplicateException.class);
    }

    @Test
    @DisplayName("비밀번호 변경시 같은 비밀번호로 변경 불가")
    void password_cannot_be_reused() {
        // given
        long userId = 1L;
        String rawPassword = "raw_password1234";
        Password password = passwordFactory.createPassword(rawPassword);
        User user = new User("", password);

        userRepository.save(user);

        UserService sut = new UserService(userRepository, passwordFactory);

        // when and then
        assertThatThrownBy(() -> sut.updatePassword(userId, rawPassword))
                .isInstanceOf(PasswordReusedException.class);
    }
}
