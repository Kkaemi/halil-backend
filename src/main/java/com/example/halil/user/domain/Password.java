package com.example.halil.user.domain;

public interface Password {

    String encode();

    boolean matches(String rawPassword);
}
