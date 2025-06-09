package com.example.halil.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.halil.auth.domain.UserInfoService;
import com.example.halil.auth.dto.JwtBundleDto;
import com.example.halil.auth.dto.LoginRequestDto;
import com.example.halil.auth.infra.AuthTokenFactoryImpl;
import com.example.halil.auth.infra.FixedTimeProvider;
import com.example.halil.auth.infra.TimeProvider;
import com.example.halil.auth.infra.UserInfoServiceImpl;
import com.example.halil.global.config.properties.JwtProperties;
import com.example.halil.user.domain.PasswordFactory;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.infra.PasswordFactoryImpl;
import com.example.halil.user.infra.UserMemoryRepository;
import java.security.SecureRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginUseCaseTest {

    private final UserInfoService userInfoService;
    private final UserRepository userRepository;
    private final PasswordFactory passwordFactory;
    private final JwtProperties jwtProperties;

    public LoginUseCaseTest() {
        this.userRepository = new UserMemoryRepository();
        this.passwordFactory = new PasswordFactoryImpl();
        this.userInfoService = new UserInfoServiceImpl(passwordFactory, userRepository);

        byte[] bytes = new byte[32];
        SecureRandom secureRandom = new SecureRandom();

        secureRandom.nextBytes(bytes);
        String accessTokenSecret = new String(bytes);

        secureRandom.nextBytes(bytes);
        String refreshTokenSecret = new String(bytes);

        this.jwtProperties = new JwtProperties(accessTokenSecret, refreshTokenSecret);
    }

    @Test
    @DisplayName("로그인하면 엑세스, 리프레시 토큰을 반환한다")
    void login() {
        // given
        String email = "test.email@email.com";
        String rawPassword = "test_password!123";
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, rawPassword);

        User user = new User(email, passwordFactory.createPassword(rawPassword));
        userRepository.save(user);

        TimeProvider timeProvider = FixedTimeProvider.of(2025, 6, 3, 12, 0, 0);
        AuthTokenFactoryImpl authTokenFactory = new AuthTokenFactoryImpl(jwtProperties, timeProvider);

        LoginUseCase sut = new LoginUseCase(authTokenFactory, userInfoService);

        // when
        JwtBundleDto jwtBundleDto = sut.login(loginRequestDto);

        // then
        assertThat(jwtBundleDto.accessToken().isValidFromNow(timeProvider.now())).isTrue();
        assertThat(jwtBundleDto.refreshToken().isValidFromNow(timeProvider.now())).isTrue();
    }
}
