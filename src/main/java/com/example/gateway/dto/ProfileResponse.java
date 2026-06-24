package com.example.gateway.dto;

import java.util.List;

public record ProfileResponse(
        String username,
        List<String> authorities
) {
}
