package org.scoula.register.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.register.domain.dto.MortgageDTO;
import org.scoula.register.domain.dto.SeizureDTO;
import org.scoula.register.service.MortgageServiceImpl;
import org.scoula.register.service.SeizureServiceImpl;
import org.scoula.register.service.TabulaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/safety-check")
public class TabulaController {

    private final TabulaService tabulaService;
    private final MortgageServiceImpl mortgageServiceImpl;
    private final SeizureServiceImpl seizureServiceImpl;

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

    @PostMapping("/mortgages")
    @ResponseBody
    public ResponseEntity<List<MortgageDTO>> extractMortgages(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            List<MortgageDTO> mortgages = mortgageServiceImpl.extractMortgageInfos(table);
            return ResponseEntity.ok(mortgages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/seizures")
    @ResponseBody
    public ResponseEntity<List<SeizureDTO>> extractSeizures(@RequestParam("file") MultipartFile file) {
        try {
            List<List<String>> table = tabulaService.extractTable(file.getInputStream());
            List<SeizureDTO> seizures = seizureServiceImpl.extractSeizureInfos(table);
            return ResponseEntity.ok(seizures);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}