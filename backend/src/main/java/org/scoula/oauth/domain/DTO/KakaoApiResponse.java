package org.scoula.oauth.domain.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoApiResponse {
    // 카카오에서 받은  ID
    private String id;

    // 추후 다른 정보 가져올 수 있으니 properties로 정의
    private Properties properties;

    @Data
    public static class Properties {
        private String nickname;
    }
}