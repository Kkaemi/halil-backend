package com.example.halil.todo;

import com.example.halil.todo.dto.TodoSaveRequestDto;
import com.example.halil.todo.dto.TodoSaveResponseDto;
import com.example.halil.todo.dto.TodoUpdateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/v1/todos")
    public TodoSaveResponseDto createTodo(@RequestBody @Valid TodoSaveRequestDto requestDto) {
        return todoService.save(requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/v1/todos/{todoId}")
    public void updateTodo(
            @PathVariable @Positive long todoId,
            @AuthenticationPrincipal long userId,
            @RequestBody @Valid TodoUpdateRequestDto requestDto
    ) {
        todoService.update(todoId, userId, requestDto);
    }
}
