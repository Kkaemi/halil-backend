package com.example.halil.user.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.halil.global.config.security.jwt.AccessTokenProviderTest;
import com.example.halil.global.config.security.jwt.JwtProviderFactory;
import com.example.halil.global.config.security.jwt.RefreshTokenProviderTest;
import com.example.halil.user.domain.Password;
import com.example.halil.user.domain.PasswordFactory;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.domain.exception.LoginException;
import com.example.halil.user.dto.LoginRequestDto;
import com.example.halil.user.infra.PasswordFactoryImpl;
import com.example.halil.user.infra.UserMemoryRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginUseCaseTest {

    private final JwtProviderFactory jwtProviderFactory;
    private final PasswordFactory passwordFactory;
    private final UserRepository userRepository;

    LoginUseCaseTest() {
        this.passwordFactory = new PasswordFactoryImpl();
        this.userRepository = new UserMemoryRepository();
        this.jwtProviderFactory = new JwtProviderFactory(
                List.of(new AccessTokenProviderTest(), new RefreshTokenProviderTest())
        );
    }

    @BeforeEach
    void setUp() {
        String email = "hello@hello.com";
        Password password = passwordFactory.createPassword("password!123");
        User user = new User(email, password);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일로 유저를 못찾으면 예외 발생")
    void not_found_by_email() {
        // given
        String email = "hi@hello.com";
        String password = "password!123";
        LoginRequestDto requestDto = new LoginRequestDto(email, password);
        LoginUseCase sut = new LoginUseCase(jwtProviderFactory, passwordFactory, userRepository);

        // when and then
        assertThatThrownBy(() -> sut.login(requestDto))
                .isInstanceOf(LoginException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
    void password_mismatch() {
        // given
        String email = "hi@hello.com";
        String password = "password!1212";
        LoginRequestDto requestDto = new LoginRequestDto(email, password);
        LoginUseCase sut = new LoginUseCase(jwtProviderFactory, passwordFactory, userRepository);

        // when and then
        assertThatThrownBy(() -> sut.login(requestDto))
                .isInstanceOf(LoginException.class);
    }
}
