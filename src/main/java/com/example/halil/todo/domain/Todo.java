package com.example.halil.todo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column
    private String description;

    @Setter
    @Column
    private LocalDateTime dueDate;

    @Setter
    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Embedded
    private Author author;

    // constructor
    public Todo(
            String title,
            LocalDateTime createdAt,
            boolean completed,
            Author author
    ) {
        this.title = title;
        this.createdAt = createdAt;
        this.completed = completed;
        this.author = author;
    }
}
