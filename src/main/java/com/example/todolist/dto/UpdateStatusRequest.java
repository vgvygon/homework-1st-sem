package com.example.todolist.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull Boolean completed
) {
}
