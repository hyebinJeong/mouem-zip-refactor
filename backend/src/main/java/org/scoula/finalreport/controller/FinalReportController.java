package org.scoula.finalreport.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.scoula.finalreport.dto.FinalReportDTO;
import org.scoula.finalreport.dto.FinalReportRawDTO;
import org.scoula.finalreport.dto.FinalReportSummaryDTO;
import org.scoula.finalreport.service.FinalReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "final-report-controller")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class FinalReportController {

    private final FinalReportService finalReportService;

    // /api/reports/{reportId}
    @ApiOperation(
            value = "Get Final Report by reportId",
            notes = "Returns a single final report using the given reportId."
    )
    @GetMapping("/{reportId}")
    @PreAuthorize("@accessChecker.canViewFinalReport(#reportId, authentication)")
    public ResponseEntity<FinalReportDTO> getFinalReport(@PathVariable Long reportId) {
        FinalReportDTO dto = finalReportService.getFinalReport(reportId);
        return ResponseEntity.ok(dto);
    }

    // 쿼리 파라미터 기반 코드 추가 /api/reports?userId=1&registryId=1
    @ApiOperation(
            value = "Get Final Report by userId and registryId",
            notes = "Fetches an existing final report using userId and registryId."
    )
    @GetMapping
    @PreAuthorize("@accessChecker.isSelf(#userId, authentication)")
    public ResponseEntity<FinalReportDTO> getReportByUserAndRegistry(
            @RequestParam("userId") Long userId,
            @RequestParam("registryId") Long registryId) {

        Long reportId = finalReportService.findReportIdByUserAndRegistry(userId, registryId);
        if (reportId == null) {
                reportId = finalReportService.createFinalReport(userId, registryId); // ← 기존 서비스 그대로 사용
        }
        FinalReportDTO dto = finalReportService.getFinalReport(reportId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(
            value = "Create Final Report",
            notes = "Creates a new final report with the specified userId and registryId."
    )
    @PostMapping
    @PreAuthorize("@accessChecker.isSelf(#userId, authentication)")
    public ResponseEntity<Long> createFinalReport(@RequestParam Long userId,
                                                  @RequestParam Long registryId) {
        Long reportId = finalReportService.createFinalReport(userId, registryId);
        return ResponseEntity.ok(reportId);
    }

    @ApiOperation(
            value = "Get Final Report List by userId",
            notes = "Retrieves a list of final reports created by the given userId."
    )

    @GetMapping("/list")
    public ResponseEntity<List<FinalReportSummaryDTO>> getReportList(@RequestParam Long userId) {
        return ResponseEntity.ok(finalReportService.getReportListByUserId(userId));
    }

}