package org.scoula.finalreport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.scoula.register.domain.dto.RegisterAnalysisResponse;

import java.util.List;

// 최종 응답용 dto
@Data
@Builder
@ApiModel(description = "Final Report response DTO")
public class FinalReportDTO {

    @ApiModelProperty(value = "User name", example = "여름")
    private String username;

    @ApiModelProperty(value = "Registry rating", example = "안전")
    private String registryRating;

    @ApiModelProperty(value = "Registry analysis result details")
    private RegisterAnalysisResponse registryAnalysis; // 등기부등본 위험요소

    @ApiModelProperty(value = "Jeonse ratio rating", example = "안전")
    private String jeonseRatioRating;

    @ApiModelProperty(value = "Jeonse ratio (%)", example = "72.5")
    private Double jeonseRatio; // 전세가율

    @ApiModelProperty(value = "Average jeonse ratio in the region (%)", example = "68.0")
    private Double regionAvgJeonseRatio; // 지역 평균 전세가율

    @ApiModelProperty(value = "Deposit amount", example = "50000000")
    private Integer deposit; // 보증금

    @ApiModelProperty(value = "Expected selling price", example = "150000000")
    private Integer expectedSellingPrice; // 예상 매매가

    @ApiModelProperty(value = "Checklist item states (true if passed, false if not)", example = "[true, false, true, ...]")
    private List<Boolean> checked; // 체크 항목 상태

    @ApiModelProperty(value = "Checklist final grade", example = "안전")
    private String checklistRating;

    // auto deploy test
}
