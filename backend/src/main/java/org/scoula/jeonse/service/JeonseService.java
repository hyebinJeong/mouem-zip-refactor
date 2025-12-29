// org/scoula/jeonse/service/JeonseService.java
package org.scoula.jeonse.service;

import org.scoula.jeonse.dto.AptRentDto;
import org.scoula.jeonse.dto.JeonseRatioDto;

import java.util.List;

public interface JeonseService {

    // 기존: 한 달치 전월세 데이터
    List<AptRentDto> getAptRent(String lawd, String yyyymm, String umdNullable);

    // 특정 년월(yyyymm) 전세가율 리스트
    List<JeonseRatioDto> getJeonseRatio(String lawd, String yyyymm, String umdNullable, String aptNullable);

    // 최근 4년(대략 48개월) 중 8개 지점 전세가율(월평균)
    List<JeonseRatioDto> getJeonseRatioRecent(String lawd, String umdNullable, String aptNullable);

    // (이미 컨트롤러에 있으니까, 연도별 월평균용 – 안 쓰면 안 불리긴 함)
    List<JeonseRatioDto> getJeonseRatioYear(String lawd, int year, String umdNullable, String aptNullable, Integer sample);
}