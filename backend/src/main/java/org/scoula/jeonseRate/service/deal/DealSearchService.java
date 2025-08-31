package org.scoula.jeonseRate.service.deal;

import org.scoula.jeonseRate.dto.DealResponseDTO;
import org.scoula.jeonseRate.dto.JeonseRateDTO;

import java.util.List;
import java.util.Optional;

/**
 * 실거래가 기반 평균 매매가 계산 서비스
 * - 아파트 → 오피스텔 → 연립다세대 순으로 실거래 데이터를 조회
 * - 입력 지번과 유사한 매물 필터링 후 평균 매매가 계산
 */
public interface DealSearchService {

    /**
     * 실거래가 평균 계산 메서드
     * @param lawdCode 법정동 코드
     * @param jibun  지번 주소
     * @param recentMonths 조회 대상 월 목록
     * @return 유사 매물 평균 매매가 (단위: 만원)
     */
    Optional<JeonseRateDTO> getDealAmount(String lawdCode, String jibun, String buildingName,
                                          List<String> recentMonths, Double targetArea);

    /**
     * 아파트 실거래가 API 호출 후 응답을 파싱하여 DTO로 반환
     * @param lawdCode 법정동 코드 (5자리, 예: "11710" → 송파구)
     * @param dealYmd  조회 기준 년월 (6자리, 예: "202406" → 2024년 6월)
     * @return DealResponseDTO 객체 (거래 내역 포함)
     */
    DealResponseDTO getDeals(String lawdCode, String dealYmd, String apiUrl);
}
