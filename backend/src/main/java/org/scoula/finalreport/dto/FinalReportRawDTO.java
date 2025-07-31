package org.scoula.finalreport.dto;

import lombok.Data;

// db 쿼리 결과 그대로 받는 dto (db 조회용)
@Data
public class FinalReportRawDTO {
    private String username;
    private String registryRating;
    private String jeonseRatioRating;
    private Double jeonseRatio;
    private Double regionAvgJeonseRatio;
    private Long deposit;
    private String checked; // json 문자열 그대로
    private String checklistRating;
}
