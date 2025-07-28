package org.scoula.oauth.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoUserDTO {
    private String kakaoId;
    private String name;
}