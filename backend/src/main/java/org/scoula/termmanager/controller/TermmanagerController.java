package org.scoula.termmanager.controller;

import java.util.List;

import org.scoula.termmanager.domain.TermmanagerVO;
import org.scoula.termmanager.service.TermmanagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/term-manager")
@CrossOrigin(origins = "*")
public class TermmanagerController {

    private final TermmanagerService termService;

    public TermmanagerController(TermmanagerService termService) {
        this.termService = termService;
    }

    @GetMapping
    public ResponseEntity<List<TermmanagerVO>> getAllTerms() {
        List<TermmanagerVO> terms = termService.getAllTerms();
        return new ResponseEntity<>(terms, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TermmanagerVO> getTerm(@PathVariable("id") Integer id) {
        TermmanagerVO term = termService.getTermById(id);
        if (term == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(term, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> addTerm(@RequestBody TermmanagerVO term) {
        termService.insertTerm(term);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTerm(@PathVariable("id") Integer id, @RequestBody TermmanagerVO term) {
        TermmanagerVO existingTerm = termService.getTermById(id);
        if (existingTerm == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        term.setTermId(id);
        termService.updateTerm(term);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerm(@PathVariable("id") Integer id) {
        TermmanagerVO existingTerm = termService.getTermById(id);
        if (existingTerm == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        termService.deleteTerm(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
