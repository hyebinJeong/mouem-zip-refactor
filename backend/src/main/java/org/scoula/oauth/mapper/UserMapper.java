package org.scoula.oauth.mapper;

import org.scoula.oauth.domain.DTO.KakaoUserDTO;

public interface UserMapper {
    KakaoUserDTO findByKakaoId(String kakaoId);
    void insertUser(KakaoUserDTO user);
}
