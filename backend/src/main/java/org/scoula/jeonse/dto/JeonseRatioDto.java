// org/scoula/jeonse/dto/JeonseRatioDto.java
package org.scoula.jeonse.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class JeonseRatioDto {
    String aptNm;      // 아파트명
    String umdNm;      // 법정동명
    Double area;       // 전용면적(㎡)
    Long deposit;      // 보증금(만원 단위 숫자)
    Long salePrice;    // 매매가(만원 단위 숫자, 여기서는 임의 계산)
    Double ratio;      // 전세가율(%)
    String ymd;        // "YYYY-MM" 또는 "YYYY-MM-DD" 형식
}