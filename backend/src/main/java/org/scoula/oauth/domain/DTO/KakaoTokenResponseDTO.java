package org.scoula.oauth.domain.DTO;

import lombok.Data;

// access_token을 발급 받고 id, name 추출
@Data
public class KakaoTokenResponseDTO {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private int refresh_token_expires_in;
    private String scope;
}