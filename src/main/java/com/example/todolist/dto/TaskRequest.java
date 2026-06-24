package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record TaskRequest(
        @NotBlank String title,
        String description,
        Boolean completed,
        LocalDate dueDate,
        Priority priority
) {
}
