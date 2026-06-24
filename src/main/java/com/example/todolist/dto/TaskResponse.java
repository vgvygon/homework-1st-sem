package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        LocalDate dueDate,
        Priority priority
) {
}
