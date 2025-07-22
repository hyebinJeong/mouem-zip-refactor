package org.scoula.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.service.TabulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/safety-check")
public class TabulaController {

    private final TabulaService tabulaService;

    @PostMapping
    @ResponseBody
    public ResponseEntity<List<List<String>>> extractTableFromPdf(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            return ResponseEntity.ok(table); // JSON 형태로 표 데이터 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}