package com.example.halil.user.service;

import com.example.halil.global.config.security.jwt.JwtClaim;
import com.example.halil.global.config.security.jwt.JwtProviderFactory;
import com.example.halil.user.domain.Password;
import com.example.halil.user.domain.PasswordFactory;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.domain.exception.LoginException;
import com.example.halil.user.dto.JwtBundleDto;
import com.example.halil.user.dto.LoginRequestDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginUseCase {

    private final JwtProviderFactory jwtProviderFactory;
    private final PasswordFactory passwordFactory;
    private final UserRepository userRepository;

    public JwtBundleDto login(LoginRequestDto requestDto) {

        String requestedEmail = requestDto.getEmail();
        String rawPassword = requestDto.getPassword();
        Password password = passwordFactory.createPassword(rawPassword);

        // 유저 조회
        User user = userRepository.findFirstByEmail(requestedEmail)
                .orElseThrow(() -> new LoginException("이메일 혹은 비밀번호가 일치하지 않습니다."));

        // 로그인
        user.login(password);

        // 토큰 생성
        JwtClaim jwtClaim = new JwtClaim(String.valueOf(user.getId()), user.getRole().name(), Instant.now());
        String accessToken = jwtProviderFactory.accessToken().generate(jwtClaim);
        String refreshToken = jwtProviderFactory.refreshToken().generate(jwtClaim);

        return new JwtBundleDto(accessToken, refreshToken);
    }
}
