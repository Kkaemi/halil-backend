package com.example.halil.auth.domain;

public interface UserService {

    UserInfo getUserInfo(String email, String password);

    void setTemporaryPassword(String email, String temporaryPassword);
}
