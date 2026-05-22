package com.example.todolist.dto;

import java.time.Instant;

public record TaskAttachmentResponse(
        Long id,
        Long taskId,
        String fileName,
        String filePath,
        String contentType,
        long fileSize,
        Instant createdAt
) {
}
