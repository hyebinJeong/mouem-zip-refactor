package org.scoula.checklist.controller;

import org.scoula.checklist.domain.dto.ChecklistDTO;
import org.scoula.checklist.service.ChecklistService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checklist")
public class ChecklistController {

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @PostMapping
    public String saveChecklist(@RequestBody ChecklistDTO dto) {
        checklistService.saveChecklist(dto);
        return "체크리스트 저장 완료";
    }
}
