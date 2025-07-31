package org.scoula.checklist.controller;

import org.scoula.checklist.domain.dto.RegistryAnalysisDTO;
import org.scoula.checklist.service.RegistryAnalysisService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registry")
public class RegistryAnalysisController {

    private final RegistryAnalysisService service;

    public RegistryAnalysisController(RegistryAnalysisService service) {
        this.service = service;
    }

    // 사용자 ID를 파라미터로 받는 방식
    @GetMapping("/user/{userId}")
    public List<RegistryAnalysisDTO> getRegistriesByUserId(@PathVariable int userId) {
        return service.getRegistryByUserId(userId);
    }
}
