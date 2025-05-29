package com.example.halil.user.domain;

import com.example.halil.user.domain.exception.PasswordCannotBeReused;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String encodedPassword;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    public User(String email, Password password, UserRole role, UserStatus userStatus) {
        this.email = email;
        this.encodedPassword = password.encode();
        this.role = role;
        this.userStatus = userStatus;
    }

    public void setTemporaryPassword(Password password) {
        this.encodedPassword = password.encode();
    }

    public void updatePassword(Password password) throws PasswordCannotBeReused {
        if (password.matches(this.encodedPassword)) {
            throw new PasswordCannotBeReused();
        }

        this.encodedPassword = password.encode();
    }

    public void withdraw() {
        this.userStatus = UserStatus.DELETED;
    }

    public void authenticateWith(Password password) {
        if (!password.matches(this.encodedPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (this.userStatus == UserStatus.DELETED) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }
    }
}
