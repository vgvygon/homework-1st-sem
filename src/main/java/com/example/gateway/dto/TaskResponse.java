package com.example.gateway.dto;

public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed
) {
}
