package com.example.halil.todo.infra;

import com.example.halil.todo.domain.Todo;
import com.example.halil.todo.domain.TodoRepository;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TodoMemoryRepository implements TodoRepository {

    private final List<Todo> todoList = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public Todo save(Todo todo) {
        try {
            long id = idGenerator.getAndIncrement();
            Field idField = Todo.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(todo, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("엔티티 아이디 리플렉션 실패", e);
        }
        todoList.add(todo);
        return todo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todoList.stream()
                .filter(todo -> todo.getId().equals(id))
                .findFirst();
    }

    @Override
    public void delete(Todo todo) {
        todoList.removeIf(t -> t.getId().equals(todo.getId()));
    }
}
