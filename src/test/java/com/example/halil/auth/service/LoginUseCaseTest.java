package com.example.halil.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.halil.auth.domain.JwtService;
import com.example.halil.auth.domain.UserInfoService;
import com.example.halil.auth.dto.JwtBundleDto;
import com.example.halil.auth.dto.LoginRequestDto;
import com.example.halil.auth.infra.JwtServiceImpl;
import com.example.halil.auth.infra.UserInfoServiceImpl;
import com.example.halil.properties.JwtProperties;
import com.example.halil.user.domain.PasswordFactory;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.infra.PasswordFactoryImpl;
import com.example.halil.user.infra.UserMemoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginUseCaseTest {

    private final JwtService jwtService;
    private final UserInfoService userInfoService;
    private final UserRepository userRepository;
    private final PasswordFactory passwordFactory;

    public LoginUseCaseTest() {
        this.userRepository = new UserMemoryRepository();
        this.passwordFactory = new PasswordFactoryImpl();
        this.jwtService = new JwtServiceImpl(
                new JwtProperties(
                        "292bb9fcbce1e08466d77030f4e57d45dc227f0312da9307a914c21a896d7da0",
                        "0331641c2ad7759e4bd4b3490efba41a5b2c55c284a8cf07a4756097415d04e5"
                )
        );
        this.userInfoService = new UserInfoServiceImpl(passwordFactory, userRepository);
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

        LoginUseCase sut = new LoginUseCase(jwtService, userInfoService);

        // when
        JwtBundleDto jwtBundleDto = sut.login(loginRequestDto);

        // then
        assertThat(jwtService.parse(jwtBundleDto.accessToken()).isValid()).isTrue();
        assertThat(jwtService.parse(jwtBundleDto.refreshToken()).isValid()).isTrue();
    }
}
