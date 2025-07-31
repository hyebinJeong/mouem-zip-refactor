package org.scoula.finalreport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.scoula.finalreport.dto.ChecklistJsonWrapper;
import org.scoula.finalreport.dto.FinalReportDTO;
import org.scoula.finalreport.dto.FinalReportRawDTO;
import org.scoula.finalreport.mapper.FinalReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinalReportServiceImpl implements FinalReportService {

    @Autowired
    private FinalReportMapper finalReportMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public FinalReportDTO getFinalReport(Long reportId) {
        FinalReportRawDTO rawDto = finalReportMapper.getFinalReport(reportId);

        ChecklistJsonWrapper wrapper;
        try {
            wrapper = objectMapper.readValue(rawDto.getChecked(), ChecklistJsonWrapper.class);
        } catch (Exception e) {
            throw new RuntimeException("Checklist JSON 파싱 오류", e);
        }

        return FinalReportDTO.builder()
                .username(rawDto.getUsername())
                .registryRating(rawDto.getRegistryRating())
                .jeonseRatioRating(rawDto.getJeonseRatioRating())
                .jeonseRatio(rawDto.getJeonseRatio())
                .regionAvgJeonseRatio(rawDto.getRegionAvgJeonseRatio())
                .deposit(rawDto.getDeposit())
                .checked(wrapper.getChecked())
                .checklistRating(rawDto.getChecklistRating())
                .build();
    }
}
