package org.scoula.jeonseRate.config;

import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

/**
 * 외부 API 요청을 위한 WebClient 설정 클래스
 */
@Configuration
public class WebClientConfig {

    /**
     * 주소 검색용 WebClient (도로명 주소 API: https://business.juso.go.kr)
     */
    @Bean
    public WebClient jusoWebClient() {
        return WebClient.builder()
                .baseUrl("https://business.juso.go.kr")
                .build();
    }

    /**
     * 공공데이터 포털 등 일반 API 요청용 WebClient
     * 응답 본문 최대 크기를 2MB로 설정
     */
    @Bean
    public WebClient webClient() {
        int maxSizeInBytes = 2 * 1024 * 1024;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(maxSizeInBytes))
                .build();

        return WebClient.builder()
                .baseUrl("https://apis.data.go.kr")
                .exchangeStrategies(strategies)
                .build();
    }

    /**
     * JSON 역직렬화 설정을 위한 ObjectMapper Bean
     * - 알 수 없는 필드 무시
     * - 빈 문자열은 null로 처리
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.coercionConfigFor(LogicalType.POJO)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
        return mapper;
    }
}
