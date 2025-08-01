package org.scoula.specialclause.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.specialclause.domain.SpecialClauseDTO;
import org.scoula.specialclause.service.SpecialClauseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/special-clauses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SpecialClauseController {

    private final SpecialClauseService service;

    @GetMapping
    public List<SpecialClauseDTO> getAll() {
        return service.getAll();
    }

    @PostMapping
    public void create(@RequestBody SpecialClauseDTO clause) {
        service.create(clause);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}
