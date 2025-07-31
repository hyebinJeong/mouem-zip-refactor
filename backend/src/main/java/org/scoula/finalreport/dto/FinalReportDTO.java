package org.scoula.finalreport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalReportDTO {
    private String username;
    private String registryRating;
    private String jeonseRatioRating;
    private Double jeonseRatio; // 전세가율
    private Double regionAvgJeonseRatio; // 지역 평균 전세가율
    private Long deposit; // 보증금
    private List<Boolean> checked; // 체크 항목 상태
    private String checklistRating;
}
