package org.scoula.finalreport.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.finalreport.dto.FinalReportDTO;
import org.scoula.finalreport.service.FinalReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reports")
@RequiredArgsConstructor
public class FinalReportController {

    private final FinalReportService finalReportService;

    // /api/reports/{reportId}
    @GetMapping("/{reportId}")
    public ResponseEntity<FinalReportDTO> getFinalReport(@PathVariable Long reportId) {
        FinalReportDTO dto = finalReportService.getFinalReport(reportId);
        return ResponseEntity.ok(dto);
    }

    // 쿼리 파라미터 기반 코드 추가 /api/reports?userId=1&registryId=1
    @GetMapping
    public ResponseEntity<FinalReportDTO> getReportByUserAndRegistry(
            @RequestParam("userId") Long userId,
            @RequestParam("registryId") Long registryId) {

        Long reportId = finalReportService.findReportIdByUserAndRegistry(userId, registryId);
        FinalReportDTO dto = finalReportService.getFinalReport(reportId);
        return ResponseEntity.ok(dto);
    }
}