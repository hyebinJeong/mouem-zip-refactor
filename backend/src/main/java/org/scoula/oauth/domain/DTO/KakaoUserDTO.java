package org.scoula.oauth.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserDTO {
    private Long userId;
    private String kakaoId;
    private String name;
    private String email;
    private boolean state;
    private String role;

    public KakaoUserDTO(String kakaoId, String name, String email, boolean state) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.email = email;
        this.state = state;
    }
}