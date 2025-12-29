package org.scoula.finalreport.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.finalreport.dto.FinalReportDTO;
import org.scoula.finalreport.dto.FinalReportInsertDTO;
import org.scoula.finalreport.dto.FinalReportRawDTO;
import org.scoula.finalreport.dto.FinalReportSummaryDTO;
import org.scoula.finalreport.mapper.FinalReportMapper;
import org.scoula.register.domain.dto.RegisterAnalysisResponse;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class FinalReportServiceImpl implements FinalReportService {

    private final FinalReportMapper finalReportMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "reports:detail", key = "#reportId")
    public FinalReportDTO getFinalReport(Long reportId) {
        FinalReportRawDTO rawDto = finalReportMapper.getFinalReport(reportId);

        if (rawDto == null) {
            throw new IllegalArgumentException("해당 리포트를 찾을 수 없습니다.");
        }

        // 체크리스트 json 파싱 (String -> List<Boolean>)
        List<Boolean> checkedList;
        try {
            checkedList = objectMapper.readValue(rawDto.getChecked(), new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("체크리스트 JSON 파싱 실패 문자열: {}", rawDto.getChecked(), e);
            throw new IllegalArgumentException("체크리스트 데이터 파싱에 실패했습니다.", e);
        }

        // 등기부 위험요소 json 파싱 (String -> RegisterAnalysisResponse)
        RegisterAnalysisResponse registerAnalysis;
        try {
            registerAnalysis = objectMapper.readValue(
                    rawDto.getAnalysisJson(),
                    RegisterAnalysisResponse.class
            );
        } catch (Exception e) {
            log.error("등기부분석 JSON 파싱 실패 문자열: {}", rawDto.getAnalysisJson(), e);
            throw new IllegalArgumentException("등기부등본 분석 데이터 파싱에 실패했습니다.", e);
        }


        // 필터링 추가 (canceled == false)
        registerAnalysis.setMortgageInfos(
                registerAnalysis.getMortgageInfos().stream()
                        .filter(info -> !info.isCanceled())
                        .toList()
        );
        registerAnalysis.setSeizureInfos(
                registerAnalysis.getSeizureInfos().stream()
                        .filter(info -> !info.isCanceled())
                        .toList()
        );
        registerAnalysis.setProvisionalSeizureInfos(
                registerAnalysis.getProvisionalSeizureInfos().stream()
                        .filter(info -> !info.isCanceled())
                        .toList()
        );
        registerAnalysis.setAuctionInfos(
                registerAnalysis.getAuctionInfos().stream()
                        .filter(info -> !info.isCanceled())
                        .toList()
        );
        registerAnalysis.setProvisionalRegistrationInfos(
                registerAnalysis.getProvisionalRegistrationInfos().stream()
                        .filter(info -> !info.isCanceled())
                        .toList()
        );
        registerAnalysis.setInjunctionInfos(
                registerAnalysis.getInjunctionInfos().stream()
                        .filter(info -> !info.isCanceled())
                        .toList()
        );
        registerAnalysis.setJeonseRightInfos(
                registerAnalysis.getJeonseRightInfos().stream()
                        .filter(info -> !info.isCanceled())
                        .toList()
        );
        registerAnalysis.setTrustInfos(
                registerAnalysis.getTrustInfos().stream()
                        .filter(info -> !info.isCanceled())
                        .toList()
        );


        return FinalReportDTO.builder()
                .username(rawDto.getUsername())
                .registryRating(rawDto.getRegistryRating())
                .registryAnalysis(registerAnalysis) // 파싱된 등기부등본 위험요소
                .jeonseRatioRating(rawDto.getJeonseRatioRating())
                .jeonseRatio(rawDto.getJeonseRatio())
                .regionAvgJeonseRatio(rawDto.getRegionAvgJeonseRatio())
                .deposit(rawDto.getDeposit())
                .expectedSellingPrice(rawDto.getExpectedSellingPrice())
                .checked(checkedList)
                .checklistRating(rawDto.getChecklistRating())
                .build();
    }

    @Override
    public Long findReportIdByUserAndRegistry(Long userId, Long registryId) {
        return finalReportMapper.findReportIdByUserAndRegistry(userId, registryId);
    }

    // finalReport에 받아온 userId, registryId 저장
    @Override
    public Long createFinalReport(Long userId, Long registryId) {
        FinalReportInsertDTO dto = new FinalReportInsertDTO();
        dto.setUserId(userId);
        dto.setRegistryId(registryId);
        finalReportMapper.insertFinalReport(dto);
        return dto.getReportId();
    }

    @Override
    @Cacheable(value = "reports:list", key = "#userId")
    public List<FinalReportSummaryDTO> getReportListByUserId(Long userId) {
        return finalReportMapper.getReportListByUserId(userId);
    }

}
