package com.example.halil.todo.query;

import com.example.halil.todo.domain.Todo;
import java.time.Instant;
import java.time.ZoneId;

public record TodoView(
        long id,
        String title,
        String description,
        Instant createdAt,
        Instant dueDate,
        boolean completed
) {

    public static TodoView of(Todo todo) {
        Instant dueDate = todo.getDueDate() == null
                ? null
                : todo.getDueDate().atZone(ZoneId.systemDefault()).toInstant();

        return new TodoView(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant(),
                dueDate,
                todo.isCompleted()
        );
    }
}
