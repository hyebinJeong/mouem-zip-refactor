package org.scoula.finalreport.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.scoula.finalreport.dto.FinalReportDTO;
import org.scoula.finalreport.dto.FinalReportRawDTO;
import org.scoula.finalreport.mapper.FinalReportMapper;
import org.scoula.finalreport.mapper.FinalReportMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinalReportServiceImpl implements FinalReportService {

    private final FinalReportMapper finalReportMapper;
    private final ObjectMapper objectMapper;

    @Override
    public FinalReportDTO getFinalReport(Long reportId) {
        FinalReportRawDTO rawDto = finalReportMapper.getFinalReport(reportId);
        if (rawDto == null) {
            throw new IllegalArgumentException("해당 리포트를 찾을 수 없습니다.");
        }

        List<Boolean> checkedList;
        try {
            checkedList = objectMapper.readValue(rawDto.getChecked(), new TypeReference<>() {});
        } catch (Exception e) {
            System.out.println("JSON 파싱 실패 문자열: " + rawDto.getChecked());
            throw new RuntimeException("Checklist JSON 파싱 오류", e);
        }

        return FinalReportDTO.builder()
                .username(rawDto.getUsername())
                .registryRating(rawDto.getRegistryRating())
                .jeonseRatioRating(rawDto.getJeonseRatioRating())
                .jeonseRatio(rawDto.getJeonseRatio())
                .regionAvgJeonseRatio(rawDto.getRegionAvgJeonseRatio())
                .deposit(rawDto.getDeposit())
                .expectedSellingPrice(rawDto.getExpectedSellingPrice())
                .checked(checkedList)
                .checklistRating(rawDto.getChecklistRating())
                .build();
    }
}
