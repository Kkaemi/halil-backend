package com.example.halil.todo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Author(
        @Column(nullable = false)
        long userId
) {

}
