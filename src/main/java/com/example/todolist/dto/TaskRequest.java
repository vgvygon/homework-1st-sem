package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record TaskRequest(
        @NotBlank String title,
        String description,
        boolean completed,
        @NotNull Priority priority,
        LocalDate dueDate,
        List<String> tags
) {
}
