package com.example.halil.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
public class Password {

    @Column(name = "password", nullable = false)
    private String value;

    public Password(String value) {
        this.value = value;
    }
}
