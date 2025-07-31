package org.scoula.oauth.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserDTO {
    private String kakaoId;
    private String name;
    private String email;
    private boolean state;
}