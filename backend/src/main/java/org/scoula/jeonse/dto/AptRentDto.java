package org.scoula.jeonse.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AptRentDto {
    String aptNm;
    Integer buildYear;
    Integer dealYear;
    Integer dealMonth;
    Integer dealDay;
    String deposit;     // "90,000" 형태 원문
    String monthlyRent; // 사용 안 해도 놔둠
    Double excluUseAr;  // 전용면적(㎡)
    Integer floor;
    String jibun;
    String sggCd;       // 시군구 코드
    String umdNm;       // 법정동
}
