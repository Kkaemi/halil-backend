package com.example.halil.todo;

import com.example.halil.todo.dto.TodoSaveRequestDto;
import com.example.halil.todo.dto.TodoSaveResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/v1/todos")
    public TodoSaveResponseDto createTodo(@RequestBody @Valid TodoSaveRequestDto requestDto) {
        return todoService.save(requestDto);
    }
}
