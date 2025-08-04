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

    private KakaoAccount kakao_account;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        private String nickname;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        private String email; // 카카오 응답 형태가 kakao_account.email
    }
}