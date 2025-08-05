package org.scoula.finalreport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// db 쿼리 결과 그대로 받는 dto (db 조회용)
@Data
@ApiModel(description = "Raw DTO returned from DB query for Final Report")
public class FinalReportRawDTO {

    @ApiModelProperty(value = "User's name", example = "여름")
    private String username;

    @ApiModelProperty(value = "Registry analysis grade", example = "안전")
    private String registryRating;

    @ApiModelProperty(value = "JSON string representing registry risk elements", example = "[\"가압류\", \"근저당\"]")
    private String analysisJson; // 등기부등본 위험요소

    @ApiModelProperty(value = "Jeonse ratio grade", example = "안전")
    private String jeonseRatioRating;

    @ApiModelProperty(value = "Jeonse ratio (%)", example = "72.5")
    private Double jeonseRatio;

    @ApiModelProperty(value = "Average jeonse ratio of the region (%)", example = "68.0")
    private Double regionAvgJeonseRatio;

    @ApiModelProperty(value = "Deposit amount in KRW", example = "50000000")
    private Integer deposit;

    @ApiModelProperty(value = "Expected selling price in KRW", example = "150000000")
    private Integer expectedSellingPrice; // 예상 매매가

    @ApiModelProperty(value = "Checklist states as a JSON string of booleans", example = "\"[true,false,true,false]\"")
    private String checked; // json 문자열 그대로

    @ApiModelProperty(value = "Checklist grade", example = "안전")
    private String checklistRating;
}
