package com.example.gateway.dto;

import java.net.URI;

public record TaskCreatedResponse(
        TaskResponse task,
        URI location
) {
}
