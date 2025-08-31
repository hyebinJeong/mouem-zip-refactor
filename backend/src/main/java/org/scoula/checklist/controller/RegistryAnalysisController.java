package org.scoula.checklist.controller;

import org.scoula.register.domain.dto.RegisterDTO;
import org.scoula.register.service.TabulaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/registry")
public class RegistryAnalysisController {

    private final TabulaService service;

    public RegistryAnalysisController(TabulaService service) {
        this.service = service;
    }

    // 사용자 ID를 파라미터로 받는 방식
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RegisterDTO>> getRegistriesByUserId(@PathVariable int userId) {
        List<RegisterDTO> list = service.findByUserId(userId);
        return ResponseEntity.ok(list);
    }
}