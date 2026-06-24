package com.example.gateway.security;

import com.example.gateway.config.JwtProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private final JwtProperties properties;
    private final ObjectMapper objectMapper;

    public JwtUtils(JwtProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        try {
            Instant now = Instant.now();
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", username);
            payload.put("iat", now.getEpochSecond());
            payload.put("exp", now.plus(properties.expiration()).getEpochSecond());
            payload.put("authorities", authorities.stream().map(GrantedAuthority::getAuthority).toList());

            String headerPart = encode(objectMapper.writeValueAsBytes(header));
            String payloadPart = encode(objectMapper.writeValueAsBytes(payload));
            String unsignedToken = headerPart + "." + payloadPart;
            String signature = encode(sign(unsignedToken));

            return unsignedToken + "." + signature;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to generate token", exception);
        }
    }

    public JwtPayload validate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format");
            }

            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = encode(sign(unsignedToken));
            if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("Invalid token signature");
            }

            Map<String, Object> payload = objectMapper.readValue(decode(parts[1]), new TypeReference<>() {
            });

            long exp = ((Number) payload.get("exp")).longValue();
            if (Instant.now().getEpochSecond() >= exp) {
                throw new IllegalArgumentException("Token expired");
            }

            String username = String.valueOf(payload.get("sub"));
            List<String> authorities = ((List<?>) payload.get("authorities"))
                    .stream()
                    .map(String::valueOf)
                    .toList();

            return new JwtPayload(username, authorities);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid token", exception);
        }
    }

    public String mask(String token) {
        if (token == null || token.length() <= 12) {
            return "******";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 6);
    }

    private byte[] sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private byte[] decode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }
}
