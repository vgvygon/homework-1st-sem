package com.example.gateway.security;

import java.util.List;

public record JwtPayload(
        String username,
        List<String> authorities
) {
}
