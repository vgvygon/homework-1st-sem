package com.example.gateway.api;

import com.example.gateway.dto.DocsResponse;
import com.example.gateway.dto.ProfileResponse;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {
    @GetMapping("/api/v1/profile")
    public ProfileResponse profile(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(Object::toString)
                .toList();
        return new ProfileResponse(authentication.getName(), authorities);
    }

    @GetMapping("/api/v1/docs")
    public DocsResponse docs() {
        return new DocsResponse("Private documents", "This endpoint requires READ_PRIVILEGE authority");
    }
}
