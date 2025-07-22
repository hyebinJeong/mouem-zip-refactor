package org.scoula.controller;

import org.scoula.service.TabulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pdf")
public class TabulaController {

    @Autowired
    private TabulaService tabulaService;

    @PostMapping("/extract-table")
    public ResponseEntity<List<List<String>>> extractTable(@RequestParam("file") MultipartFile file) {
        try {
            // 임시파일로 변환
            File tempFile = File.createTempFile("upload-", ".pdf");
            file.transferTo(tempFile);

            List<List<String>> tableData = tabulaService.extractTableFromPdf(tempFile);

            // 임시파일 삭제
            tempFile.delete();

            return ResponseEntity.ok(tableData);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}