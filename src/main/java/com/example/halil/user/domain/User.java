package com.example.halil.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Embedded
    private Password password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(String email, Password password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void setTemporarilyPassword(String encodedTempPassword) {
        this.password = new Password(encodedTempPassword);
    }
}
