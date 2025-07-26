package org.scoula.jeonseRate.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 프론트에서 전세가율 분석을 요청할 때 전송하는 요청 데이터 DTO
 */
@Getter
@Setter
@ToString
public class LeaseRequestDTO {
    private String address;         // 사용자가 입력한 주소
    private String jeonsePrice;     // 전세 보증금 (단위: 만원)
    private String isApt;           // 아파트 여부 ("Y" 또는 "N" 등으로 사용) - 유형 분류에 사용됨
}