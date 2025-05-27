package com.example.halil.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoSaveRequestDto {

    @Size(max = 255)
    @NotBlank
    private String title;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private Boolean completed;

    private LocalDateTime dueDate;

    @Size(max = 255)
    private String description;
}
