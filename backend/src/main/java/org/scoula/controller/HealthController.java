package org.scoula.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // REST 응답(문자/JSON)을 바로 반환하는 컨트롤러
public class HealthController {

    // GET /api/ping 으로 매핑
    @GetMapping("/api/ping")
    public String ping() {
        return "OK";   // curl로 호출하면 "OK" 문자열이 그대로 응답으로 나감
    }
}
