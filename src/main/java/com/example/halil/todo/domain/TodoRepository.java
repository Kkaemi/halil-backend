package com.example.halil.todo.domain;

import java.util.Optional;

public interface TodoRepository {

    Todo save(Todo todo);

    Optional<Todo> findById(Long id);

    void delete(Todo todo);
}
