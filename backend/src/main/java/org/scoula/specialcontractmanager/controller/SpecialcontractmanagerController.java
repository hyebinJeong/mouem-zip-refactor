package org.scoula.specialcontractmanager.controller;

import java.util.List;

import org.scoula.specialcontractmanager.domain.SpecialcontractmanagerVO;
import org.scoula.specialcontractmanager.service.SpecialcontractmanagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/special-clauses")
@CrossOrigin(origins = "*") // CORS 설정 (필요시 조정)
public class SpecialcontractmanagerController {

    private final SpecialcontractmanagerService service;

    public SpecialcontractmanagerController(SpecialcontractmanagerService service) {
        this.service = service;
    }

    @GetMapping
    public List<SpecialcontractmanagerVO> getAll() {
        return service.getAllSpecialClauses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialcontractmanagerVO> getById(@PathVariable Integer id) {
        SpecialcontractmanagerVO vo = service.getSpecialClauseById(id);
        if (vo == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(vo);
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody SpecialcontractmanagerVO vo) {
        boolean created = service.addSpecialClause(vo);
        if (created) return ResponseEntity.ok("Created");
        else return ResponseEntity.badRequest().body("Failed to create");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody SpecialcontractmanagerVO vo) {
        vo.setSpecialClauseId(id);
        boolean updated = service.updateSpecialClause(vo);
        if (updated) return ResponseEntity.ok("Updated");
        else return ResponseEntity.badRequest().body("Failed to update");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        boolean deleted = service.deleteSpecialClause(id);
        if (deleted) return ResponseEntity.ok("Deleted");
        else return ResponseEntity.badRequest().body("Failed to delete");
    }
}
