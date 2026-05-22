package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        Priority priority,
        LocalDate dueDate,
        List<String> tags,
        List<TaskAttachmentResponse> attachments,
        Instant createdAt,
        Instant updatedAt
) {
}
