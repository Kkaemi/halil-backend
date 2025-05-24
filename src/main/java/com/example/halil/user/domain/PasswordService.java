package com.example.halil.user.domain;

public interface PasswordService {

    Password encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
