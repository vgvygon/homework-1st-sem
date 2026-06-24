package com.example.gateway.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external-api")
public record ExternalApiProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout,
        String userAgent
) {
}
