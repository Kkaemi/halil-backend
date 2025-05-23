package com.example.halil.auth.domain;

public interface MailService {

    void sendPasswordResetMail(String to, String temporaryPassword);
}
