package org.scoula.finalreport.service;

import org.scoula.finalreport.dto.FinalReportDTO;

public interface FinalReportService {
    FinalReportDTO getFinalReport(Long reportId);
}
