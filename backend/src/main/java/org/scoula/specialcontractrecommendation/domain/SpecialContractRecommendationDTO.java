package org.scoula.specialcontractrecommendation.domain;

import lombok.Data;

@Data
public class SpecialContractRecommendationDTO {
    private int specialClauseId;       // 특약 ID
    private String category;           // 특약 분류
    private String importance;         // 중요도
    private String importanceColor;    // 중요도 색상
    private String description;        // 특약 설명
}
