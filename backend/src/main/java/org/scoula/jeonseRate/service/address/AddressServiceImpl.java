package org.scoula.jeonseRate.service.address;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.scoula.jeonseRate.dto.AddressInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    // 주소 검색용 WebClient
    private final WebClient jusoWebClient;

    // application.properties에 설정된 주소 API 인증키
    @Value("${juso.api.key}")
    private String jusoApiKey;

    // 사용자가 입력한 키워드(주소)를 기반으로 주소 정보 조회(법정동 코드, 지번, 건물명)
    @Override
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

        JSONObject json = new JSONObject(response)
                .getJSONObject("results")
                .getJSONArray("juso")
                .getJSONObject(0);

        String admCd = json.getString("admCd");               // 법정동 코드
        String lnbrMnnm = json.getString("lnbrMnnm");         // 지번 본번
        String lnbrSlno = json.getString("lnbrSlno");         // 지번 부번
        String bdNm = json.optString("bdNm", "");   // 건물명

        // 부번이 0이면 생략, 아니면 "본번-부번" 형태
        String jibun = lnbrSlno.equals("0") ? lnbrMnnm : lnbrMnnm + "-" + lnbrSlno;

        String sggNm = json.getString("sggNm");
        String siNm = json.getString("siNm");

        return new AddressInfoDTO(admCd, jibun, siNm, bdNm, sggNm);
    }
}
