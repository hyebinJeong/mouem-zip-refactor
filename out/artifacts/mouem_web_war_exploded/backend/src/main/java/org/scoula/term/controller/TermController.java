package org.scoula.term.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.term.domain.TermDTO;
import org.scoula.term.service.TermService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dictionary")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Vue 연동 시 필요
public class TermController {

    private final TermService termService;

    @GetMapping
    public List<TermDTO> getTerms(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Integer categoryId) {
        return termService.findAll(keyword, categoryId);
    }

    @GetMapping("/{id}")
    public TermDTO getTerm(@PathVariable int id) {
        return termService.findById(id);
    }
}