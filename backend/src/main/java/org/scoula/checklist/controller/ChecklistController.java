package org.scoula.checklist.controller;

import org.scoula.checklist.domain.dto.ChecklistDTO;
import org.scoula.checklist.service.ChecklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getChecklistByUserId(@PathVariable int userId) {
        try {
            List<ChecklistDTO> list = checklistService.findByUserId(userId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
