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

    public User(String email, String encodedPassword, UserRole role, UserStatus userStatus) {
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.role = role;
        this.userStatus = userStatus;
    }

    public void setTemporarilyPassword(String encodedTempPassword) {
        this.encodedPassword = encodedTempPassword;
    }

    public void updatePassword(
            PasswordService passwordService,
            String rawPassword
    ) throws PasswordCannotBeReused {
        if (passwordService.matches(rawPassword, this.encodedPassword)) {
            throw new PasswordCannotBeReused();
        }
        this.encodedPassword = passwordService.encode(rawPassword);
    }

    public void withdraw() {
        this.userStatus = UserStatus.DELETED;
    }
}
