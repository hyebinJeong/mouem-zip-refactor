package org.scoula.finalreport.service;

import org.scoula.finalreport.dto.FinalReportDTO;
import org.scoula.finalreport.dto.FinalReportSummaryDTO;

import java.util.List;

public interface FinalReportService {
    FinalReportDTO getFinalReport(Long reportId);
    Long findReportIdByUserAndRegistry(Long userId, Long registryId);
    Long createFinalReport(Long userId, Long registryId);
    List<FinalReportSummaryDTO> getReportListByUserId(Long userId);
}
