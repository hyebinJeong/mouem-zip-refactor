package org.scoula.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // 헬스체크 엔드포인트: http://서버:8080/api/ping
    @GetMapping("/api/ping")
    public String ping() {
        return "OK";
    }
}
