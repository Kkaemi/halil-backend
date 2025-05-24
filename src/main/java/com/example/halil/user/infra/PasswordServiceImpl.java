package com.example.halil.user.infra;

import com.example.halil.user.domain.Password;
import com.example.halil.user.domain.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Password encode(String rawPassword) {
        return new Password(passwordEncoder.encode(rawPassword));
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
