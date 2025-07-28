package org.scoula.jeonseRate.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.scoula.jeonseRate.dto.AddressInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 도로명 또는 지번 주소를 입력 받아 행정안전부 주소 API를 통해
 * 법정동 코드와 지번 정보를 조회하는 서비스
 */
@Service
@RequiredArgsConstructor
public class AddressService {

    // 주소 검색용 WebClient
    private final WebClient jusoWebClient;

    // application.properties에 설정된 주소 API 인증키
    @Value("${juso.api.key}")
    private String jusoApiKey;

    /**
     * 사용자가 입력한 키워드(주소)를 기반으로 주소 정보 조회
     * @param keyword 도로명 주소 또는 지번 주소
     * @return 법정동코드(admCd)와 지번(jibun)을 포함한 AddressInfoDTO 반환
     */
    public AddressInfoDTO lookupAddress(String keyword) {

        String response = jusoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/addrlink/addrLinkApi.do")
                        .queryParam("confmKey", jusoApiKey)
                        .queryParam("currentPage", "1")
                        .queryParam("countPerPage", "1")
                        .queryParam("keyword", keyword)
                        .queryParam("resultType", "json")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

//        System.out.println("AddressSerivce파일 - JUSO 응답: " + response);

        JSONObject json = new JSONObject(response)
                .getJSONObject("results")
                .getJSONArray("juso")
                .getJSONObject(0);

        String admCd = json.getString("admCd");       // 법정동 코드
        String lnbrMnnm = json.getString("lnbrMnnm"); // 지번 본번 (예: "595")
        String lnbrSlno = json.getString("lnbrSlno"); // 지번 부번 (예: "28")

        // 부번이 0이면 생략, 아니면 "본번-부번" 형태로 조합
        String jibun = lnbrSlno.equals("0") ? lnbrMnnm : lnbrMnnm + "-" + lnbrSlno;

        return new AddressInfoDTO(admCd, jibun);
    }
}
