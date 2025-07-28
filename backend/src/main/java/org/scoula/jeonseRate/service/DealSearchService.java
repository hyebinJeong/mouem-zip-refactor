package org.scoula.jeonseRate.service;

import lombok.RequiredArgsConstructor;
import org.scoula.jeonseRate.dto.DealResponseDTO;
import org.scoula.jeonseRate.dto.DealDTO;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 실거래가 기반 평균 매매가 계산 서비스
 * - 아파트 → 연립다세대 → 오피스텔 순으로 실거래 데이터를 조회
 * - 입력 지번과 유사한 매물 필터링 후 평균 매매가 계산
 */
@Service
@RequiredArgsConstructor
public class DealSearchService {

    // 부동산 유형별 실거래가 API 서비스 주입
    private final ApartmentApiService apartmentApiService;
    private final RowHouseApiService rowhouseApiService;
    private final OfficetelApiService officeApiService;

    /**
     * 실거래가 평균 계산 메서드
     * @param lawdCode 법정동 코드
     * @param jibunHead  지번 주소
     * @param recentMonths 조회 대상 월 목록 
     * @return 유사 매물 평균 매매가 (단위: 만원)
     */
    public Optional<Integer> getAverageDealAmount(String lawdCode, String jibunHead , List<String> recentMonths) {
        List<DealDTO> allDeals = new ArrayList<>();

        // 1. 아파트 실거래가 조회
        for (String month : recentMonths) {
            DealResponseDTO response = apartmentApiService.getAptDeals(lawdCode, month);
            allDeals.addAll(extractDeals(response));
        }
        
        // 2. 아파트 매물 없으면 연립다세대 조회
        if (allDeals.isEmpty()) {
            for (String month : recentMonths) {
                DealResponseDTO response = rowhouseApiService.getRowhouseDeals(lawdCode, month);
                allDeals.addAll(extractDeals(response));
            }
        }

        // 3. 연립다세대 매물 없으면 오피스텔 조회
        if (allDeals.isEmpty()) {
            for (String month : recentMonths) {
                DealResponseDTO response = officeApiService.getOfficeDeals(lawdCode, month);
                allDeals.addAll(extractDeals(response));
            }
        }

        // 모든 유형에서 매물이 없다면 판단 보류
        if (allDeals.isEmpty()) {
//            System.out.println("❌ 모든 유형에서 실거래 매물 없음 → 판단 보류");
            return Optional.empty();
        }


        String inputJibunHead = jibunHead.split("-")[0].trim(); // 예: "595-28" → "595"


//        System.out.println("📌 [매매가 조회] 검색 지번: " + inputJibunHead + ", 조회 매물 수: " + allDeals.size());


        // 입력 지번과 유사한 매물 필터링 (지번 앞자리 기준)
        List<DealDTO> filtered = allDeals.stream()
                .filter(d -> d.getJibun() != null && d.getJibun().startsWith(inputJibunHead))
                .collect(Collectors.toList());


        // 유사 매물이 없다면 판단 보류
        if (filtered.isEmpty()) {
//            System.out.println("유사 지번 매물 없음 → 판단 보류");
            return Optional.empty();
        }

        // 평균 매매가 계산 (단위: 만원)
        double avg = filtered.stream()
                .mapToDouble(d -> Double.parseDouble(d.getDealAmount().replace(",", "")))
                .average()
                .orElse(0);

        // DB 저장용: 정수로 반올림
        int roundedAvg = (int) Math.round(avg);

        //System.out.println("유사 매물 평균 매매가: " + roundedAvg + "만원");
        return Optional.of(roundedAvg);
    }

    /**
     * DealResponseDTO에서 거래 리스트(List<DealDTO>) 안전하게 추출
     * - 응답 구조가 비어 있거나 예외 발생 시 빈 리스트 반환
     */
    private List<DealDTO> extractDeals(DealResponseDTO dto) {
        try {
            if (dto.getResponse() == null ||
                    dto.getResponse().getBody() == null ||
                    dto.getResponse().getBody().getItems() == null ||
                    dto.getResponse().getBody().getItems().getItem() == null) {
                //System.out.println("실거래 응답에서 items가 비어 있음 → 빈 리스트 반환");
                return List.of();
            }

            return dto.getResponse().getBody().getItems().getItem();
        } catch (Exception e) {
//            System.out.println("응답 파싱 실패 → 빈 리스트 반환");
            return List.of();
        }
    }
}
