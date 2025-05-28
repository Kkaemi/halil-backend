package com.example.halil.todo.infra;

import com.example.halil.todo.domain.Todo;
import com.example.halil.todo.domain.TodoRepository;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TodoMemoryRepository implements TodoRepository {

    private final Map<Long, Todo> todoMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public Todo save(Todo todo) {
        long id = idGenerator.getAndIncrement();
        try {
            Field idField = Todo.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(todo, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("엔티티 아이디 리플렉션 실패", e);
        }
        todoMap.put(id, todo);
        return todo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(todoMap.get(id));
    }

    @Override
    public void delete(Todo todo) {
        todoMap.remove(todo.getId());
    }
}
