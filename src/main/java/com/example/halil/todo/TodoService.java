package com.example.halil.todo;

import com.example.halil.todo.dto.TodoSaveRequestDto;
import com.example.halil.todo.dto.TodoSaveResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoSaveResponseDto save(TodoSaveRequestDto requestDto) {
        Todo todo = new Todo(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getDueDate(),
                requestDto.getCreatedAt(),
                requestDto.isCompleted()
        );

        todoRepository.save(todo);

        return new TodoSaveResponseDto(todo.getId());
    }
}
