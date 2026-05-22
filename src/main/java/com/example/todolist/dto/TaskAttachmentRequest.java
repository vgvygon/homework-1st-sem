package com.example.todolist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record TaskAttachmentRequest(
        @NotBlank String fileName,
        @NotBlank String filePath,
        String contentType,
        @PositiveOrZero long fileSize
) {
}
