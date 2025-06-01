package com.example.halil.user.infra;

import com.example.halil.user.domain.Password;
import com.example.halil.user.domain.PasswordFactory;
import org.springframework.stereotype.Component;

@Component
public class PasswordFactoryImpl implements PasswordFactory {

    @Override
    public Password createPassword(String rawPassword) {
        return new BCryptPassword(rawPassword);
    }
}
