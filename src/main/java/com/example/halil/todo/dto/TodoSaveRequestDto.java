package com.example.halil.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TodoSaveRequestDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime dueDate;

    @NotNull
    private boolean completed;
}
