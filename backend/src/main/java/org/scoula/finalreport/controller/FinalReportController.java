package org.scoula.finalreport.controller;

import org.scoula.finalreport.dto.FinalReportDTO;
import org.scoula.finalreport.service.FinalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reports")
public class FinalReportController {

    @Autowired
    private FinalReportService finalReportService;

    @GetMapping("/{reportId}")
    public ResponseEntity<FinalReportDTO> getFinalReport(@PathVariable Long reportId) {
        FinalReportDTO dto = finalReportService.getFinalReport(reportId);
        return ResponseEntity.ok(dto);
    }
}
