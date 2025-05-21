package com.example.halil.auth.domain;

public record UserInfo(
        long userId,
        String password,
        String role
) {

}
