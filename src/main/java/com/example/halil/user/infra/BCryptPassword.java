package com.example.halil.user.infra;

import com.example.halil.user.domain.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class BCryptPassword implements Password {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final String rawPassword;

    @Override
    public String encode() {
        return PASSWORD_ENCODER.encode(rawPassword);
    }

    @Override
    public boolean matches(String encodedPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}
