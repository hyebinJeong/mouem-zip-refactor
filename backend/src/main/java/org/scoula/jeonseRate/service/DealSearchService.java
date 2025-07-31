package org.scoula.jeonseRate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.scoula.jeonseRate.dto.DealResponseDTO;
import org.scoula.jeonseRate.dto.DealDTO;
import org.scoula.jeonseRate.dto.JeonseRateDTO;
import org.scoula.jeonseRate.enums.HouseTypeCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    // 공공데이터 포털 API 호출용 WebClient (baseUrl은 WebClientConfig에서 설정)
    private final WebClient webClient;
    // JSON 응답을 DealResponseDTO로 파싱하기 위한 Jackson ObjectMapper
    private final ObjectMapper objectMapper;

    @Value("${openapi.key}")
    private String apiKey;

    @Value("${openapi.apt.url}")
    private String aptApiUrl;

    @Value("${openapi.office.url}")
    private String officeApiUrl;

    @Value("${openapi.rowhouse.url}")
    private String rowhouseApiUrl;
    /**
     * 실거래가 평균 계산 메서드
     * @param lawdCode 법정동 코드
     * @param jibun  지번 주소
     * @param recentMonths 조회 대상 월 목록
     * @return 유사 매물 평균 매매가 (단위: 만원)
     */
    public Optional<JeonseRateDTO> getDealAmount(String lawdCode, String jibun , List<String> recentMonths) {
        JeonseRateDTO jeonseRateDTO = new JeonseRateDTO();
        List<DealDTO> allDeals = new ArrayList<>();

        // 1. 아파트 실거래가 조회
        for (String month : recentMonths) {
            DealResponseDTO response = getDeals(lawdCode, month, aptApiUrl);
            allDeals.addAll(extractDeals(response));
        }
        if (!allDeals.isEmpty()) {
            jeonseRateDTO.setBuildingType(HouseTypeCode.APARTMENT.getName());
        }

        // 2. 아파트 매물 없으면 연립다세대 조회
        if (allDeals.isEmpty()) {
            for (String month : recentMonths) {
                DealResponseDTO response = getDeals(lawdCode, month, officeApiUrl);
                allDeals.addAll(extractDeals(response));
            }
            if (!allDeals.isEmpty()) {
                jeonseRateDTO.setBuildingType(HouseTypeCode.MULTI_HOUSE.getName());
            }
        }

        // 3. 연립다세대 매물 없으면 오피스텔 조회
        if (allDeals.isEmpty()) {
            for (String month : recentMonths) {
                DealResponseDTO response = getDeals(lawdCode, month, rowhouseApiUrl);
                allDeals.addAll(extractDeals(response));
            }
            if (!allDeals.isEmpty()) {
                jeonseRateDTO.setBuildingType(HouseTypeCode.OPISTEL.getName());
            }
        }

        // 모든 유형에서 매물이 없다면 판단 보류
        if (allDeals.isEmpty()) {
//            System.out.println("모든 유형에서 실거래 매물 없음 → 판단 보류");
            return Optional.empty();
        }

        // 지번과 같은 매물 필터링
        List<DealDTO> filtered = allDeals.stream()
                .filter(d -> d.getJibun() != null && d.getJibun().equals(jibun))
                .collect(Collectors.toList());

//        System.out.println("📌 [매매가 조회] 검색 지번: " + jibun + ", 조회 매물 수: " + allDeals.size());

        // 매물이 없다면 판단 보류
        if (filtered.isEmpty()) {
//            System.out.println("같은 지번 매물 없음 → 판단 보류");
            return Optional.empty();
        }

        // 평균 매매가 계산 (단위: 만원)
        double avg = filtered.stream()
                .mapToDouble(d -> Double.parseDouble(d.getDealAmount().replace(",", "")))
                .average()
                .orElse(0);

        // DB 저장용: 정수로 반올림
        jeonseRateDTO.setAreaAVGPrice((int) Math.round(avg));

        //System.out.println("유사 조건 매물 평균 매매가: " + roundedAvg + "만원");
        return Optional.of(jeonseRateDTO);
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


    /**
     * 아파트 실거래가 API 호출 후 응답을 파싱하여 DTO로 반환
     * @param lawdCode 법정동 코드 (5자리, 예: "11710" → 송파구)
     * @param dealYmd  조회 기준 년월 (6자리, 예: "202406" → 2024년 6월)
     * @return DealResponseDTO 객체 (거래 내역 포함)
     */
    public DealResponseDTO getDeals(String lawdCode, String dealYmd, String apiUrl) {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(apiUrl)
                        .queryParam("serviceKey", apiKey)       // 인증키
                        .queryParam("LAWD_CD", lawdCode)        // 법정동 코드
                        .queryParam("DEAL_YMD", dealYmd)        // 거래 연월
                        .queryParam("_type", "json")    // 응답 형식
                        .queryParam("numOfRows", "1000")// 최대 1000건까지 요청
                        .build())
                .retrieve()
                .bodyToMono(String.class)   // 응답 본문을 문자열로 받음
                .block();

//        System.out.println("매물->" + response);

        try {
            return objectMapper.readValue(response, DealResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("DealSearchService.java -> JSON 파싱 실패: " + e.getMessage(), e);
        }
    }

}
