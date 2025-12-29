package org.scoula.jeonse.infra;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.scoula.jeonse.domain.AptRentResponse;
import org.scoula.jeonse.domain.AptTradeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
public class JeonseOpenApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Value("${jeonse.api.base-url}")
    private String baseUrl;

    @Value("${jeonse.api.key}")
    private String serviceKey;

    public AptRentResponse callAptRent(String lawd, String yyyymm, int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/getRTMSDataSvcAptRent")
                .queryParam("serviceKey", serviceKey)
                .queryParam("LAWD_CD", lawd)
                .queryParam("DEAL_YMD", yyyymm)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .build(false).encode().toUri();

        log.info("[APT-RENT API] {}", uri);
        ResponseEntity<String> res = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(new LinkedMultiValueMap<>(), new HttpHeaders()), String.class);

        if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
            try {
                return xmlMapper.readValue(res.getBody(), AptRentResponse.class);
            } catch (Exception e) {
                throw new RuntimeException("XML 파싱 실패(전월세): " + e.getMessage(), e);
            }
        }
        throw new RuntimeException("전월세 API 호출 실패: " + res.getStatusCode());
    }

    public AptTradeResponse callAptTrade(String lawd, String yyyymm, int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/getRTMSDataSvcAptTrade")
                .queryParam("serviceKey", serviceKey)
                .queryParam("LAWD_CD", lawd)
                .queryParam("DEAL_YMD", yyyymm)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .build(false).encode().toUri();

        log.info("[APT-TRADE API] {}", uri);
        ResponseEntity<String> res = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(new LinkedMultiValueMap<>(), new HttpHeaders()), String.class);

        if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
            try {
                return xmlMapper.readValue(res.getBody(), AptTradeResponse.class);
            } catch (Exception e) {
                throw new RuntimeException("XML 파싱 실패(매매): " + e.getMessage(), e);
            }
        }
        throw new RuntimeException("매매 API 호출 실패: " + res.getStatusCode());
    }
}