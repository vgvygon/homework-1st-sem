package com.example.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.gateway.config.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class JwtUtilsTest {
    @Test
    void shouldGenerateAndValidateToken() {
        JwtUtils jwtUtils = new JwtUtils(new JwtProperties("test-secret-test-secret-test-secret", Duration.ofMinutes(30)), new ObjectMapper());

        String token = jwtUtils.generateToken("user", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        JwtPayload payload = jwtUtils.validate(token);

        assertThat(payload.username()).isEqualTo("user");
        assertThat(payload.authorities()).containsExactly("ROLE_USER");
    }

    @Test
    void shouldRejectInvalidSignature() {
        JwtUtils jwtUtils = new JwtUtils(new JwtProperties("test-secret-test-secret-test-secret", Duration.ofMinutes(30)), new ObjectMapper());
        String token = jwtUtils.generateToken("user", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        assertThatThrownBy(() -> jwtUtils.validate(token + "x"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
