package org.scoula.jeonseRate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 주소 검색 API(Kakao 또는 Juso.go.kr) 결과를 매핑하는 DTO 클래스
 */
@Getter
@Setter
@AllArgsConstructor
public class AddressInfoDTO {
    private String admCd;       // 법정동 코드
    private String jibunAddr;   // 지번 주소
    private String siNm;        // 시도명
    private String bdNm;        // 건물명
    private String sggNm;       // 구명
}
