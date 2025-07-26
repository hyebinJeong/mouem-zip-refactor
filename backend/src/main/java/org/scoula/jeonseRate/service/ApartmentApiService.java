package org.scoula.jeonseRate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.scoula.jeonseRate.dto.DealResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 아파트 실거래가 데이터를 국토교통부 Open API를 통해 조회하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ApartmentApiService {

    // 공공데이터 포털 API 호출용 WebClient (baseUrl은 WebClientConfig에서 설정)
    private final WebClient webClient;
    // JSON 응답을 DealResponseDTO로 파싱하기 위한 Jackson ObjectMapper
    private final ObjectMapper objectMapper;

    @Value("${openapi.key}")
    private String apiKey;

    @Value("${openapi.apt.url}")
    private String aptApiUrl;

    /**
     * 아파트 실거래가 API 호출 후 응답을 파싱하여 DTO로 반환
     * @param lawdCode 법정동 코드 (5자리, 예: "11710" → 송파구)
     * @param dealYmd  조회 기준 년월 (6자리, 예: "202406" → 2024년 6월)
     * @return DealResponseDTO 객체 (거래 내역 포함)
     */
    public DealResponseDTO getAptDeals(String lawdCode, String dealYmd) {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(aptApiUrl.replace("https://apis.data.go.kr", ""))  // path만 추출
                        .queryParam("serviceKey", apiKey)       // 인증키
                        .queryParam("LAWD_CD", lawdCode)        // 법정동 코드
                        .queryParam("DEAL_YMD", dealYmd)        // 거래 연월
                        .queryParam("_type", "json")    // 응답 형식
                        .queryParam("numOfRows", "1000")// 최대 1000건까지 요청
                        .build())
                .retrieve()
                .bodyToMono(String.class)   // 응답 본문을 문자열로 받음
                .block();

        System.out.println("[API 호출 완료] 유형: 아파트/연립/오피스텔, 응답 길이: " + response.length());

        try {
            return objectMapper.readValue(response, DealResponseDTO.class);
        } catch (Exception e) {
            System.out.println("❌ JSON 파싱 실패: " + e.getMessage());
            throw new RuntimeException("아파트 실거래가 응답 파싱 실패", e);
        }
    }
}
