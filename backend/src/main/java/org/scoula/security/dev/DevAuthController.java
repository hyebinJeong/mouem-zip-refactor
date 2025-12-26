package org.scoula.security.dev;

import lombok.RequiredArgsConstructor;
import org.scoula.security.util.JwtProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class DevAuthController {
    private final JwtProcessor jwtProcessor;

    @GetMapping("/test-token")
    public ResponseEntity<Map<String, String>> testToken() {
        // 원하는 값으로 고치면 됨 (userId, kakaoId, role)
        String jwt = jwtProcessor.generateAccessToken("1", "4373499343", "ROLE_USER");
        return ResponseEntity.ok(Map.of("accessToken", jwt));
    }
}
