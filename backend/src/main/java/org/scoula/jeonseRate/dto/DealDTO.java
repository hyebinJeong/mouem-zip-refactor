package org.scoula.jeonseRate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * 국토부 실거래가 API에서 거래 정보를 매핑하는 DTO 클래스
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DealDTO {
    private String dealAmount;  // 거래금액 (단위 : 만원)
    private String dealYear;    // 거래 연도
    private String dealMonth;   // 거래 월
    private String jibun;       // 지번
    private String excluUseAr;  // 전용면적
    private String aptDong;     // 동 정보
    private String sggCd;       // 시군구 코드
    private String aptNm;       // 아파트명
    private String offiNm;      // 오피스텔명
    private String mhouseNm;    // 연립다세대명
}
