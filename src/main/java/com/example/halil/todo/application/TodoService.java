package com.example.halil.todo.application;

import com.example.halil.todo.domain.Author;
import com.example.halil.todo.domain.Todo;
import com.example.halil.todo.domain.TodoRepository;
import com.example.halil.todo.dto.TodoSaveRequestDto;
import com.example.halil.todo.dto.TodoSaveResponseDto;
import com.example.halil.todo.dto.TodoUpdateRequestDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoSaveResponseDto save(long userId, TodoSaveRequestDto requestDto) {

        // required fields
        String title = requestDto.getTitle();
        LocalDateTime createdAt = requestDto.getCreatedAt();
        boolean completed = requestDto.getCompleted();
        Author author = new Author(userId);

        // optional fields
        String descriptionOrNull = requestDto.getDescription();
        LocalDateTime dueDateOrNull = requestDto.getDueDate();

        Todo todo = new Todo(title, createdAt, completed, author);
        todo.setDueDate(dueDateOrNull);
        todo.setDescription(descriptionOrNull);

        todoRepository.save(todo);

        return new TodoSaveResponseDto(todo.getId());
    }

    public void update(long todoId, Author editor, TodoUpdateRequestDto requestDto) {

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(TodoErrorCode.TODO_NOT_FOUND::exception);

        if (!todo.getAuthor().equals(editor)) {
            throw TodoErrorCode.ACCESS_DENIED.exception();
        }

        Boolean completed = requestDto.getCompleted();
        String description = requestDto.getDescription();
        String title = requestDto.getTitle();
        LocalDateTime dueDate = requestDto.getDueDate();

        todo.setCompleted(completed);
        todo.setDescription(description);
        todo.setTitle(title);
        todo.setDueDate(dueDate);
    }

    public void delete(long todoId, Author deleter) {

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(TodoErrorCode.TODO_NOT_FOUND::exception);

        if (!todo.getAuthor().equals(deleter)) {
            throw TodoErrorCode.ACCESS_DENIED.exception();
        }

        todoRepository.delete(todo);
    }
}
