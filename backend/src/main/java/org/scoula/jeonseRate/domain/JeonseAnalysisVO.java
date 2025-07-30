package org.scoula.jeonseRate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.scoula.jeonseRate.enums.SafetyGrade;

@Getter
@Setter
@ToString
public class JeonseAnalysisVO {
    private int registryId;
    private int userId;
    private int expectedSellingPrice;       // 예상 매매가
    private int deposit;                    // 전세 보증금
    private int jeonseRatio;                // 전세가율
    private int regionAvgJeonseRatio;       // 지역 평균 전세가율
    private SafetyGrade jeonseRatioRating;  // 등급
}
