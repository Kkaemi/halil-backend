package com.example.halil.user.service;

public interface MailService {

    void sendPasswordResetMail(String to, String temporaryPassword);
}
