package com.example.halil.todo.infra;

import com.example.halil.todo.domain.Todo;
import com.example.halil.todo.domain.TodoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TodoRepositoryImpl implements TodoRepository {

    private final TodoJpaRepository todoJpaRepository;

    @Override
    public Todo save(Todo todo) {
        return todoJpaRepository.save(todo);
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todoJpaRepository.findById(id);
    }

    @Override
    public void delete(Todo todo) {
        todoJpaRepository.delete(todo);
    }
}
