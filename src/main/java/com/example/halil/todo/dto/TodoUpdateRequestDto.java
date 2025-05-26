package com.example.halil.todo.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TodoUpdateRequestDto {

    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String description;

    private LocalDateTime dueDate;

    private Boolean completed;
}
