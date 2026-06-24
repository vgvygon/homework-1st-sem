package com.example.gateway.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}
