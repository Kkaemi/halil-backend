package com.example.halil.todo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TodoUpdateRequestDto {

    @Size(max = 255)
    @NotNull
    private String title;

    @Size(max = 255)
    @NotNull
    private String description;

    @NotNull
    private LocalDateTime dueDate;

    @NotNull
    private Boolean completed;
}
