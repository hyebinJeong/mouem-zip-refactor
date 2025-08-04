package org.scoula.oauth.mapper;

import org.scoula.oauth.domain.DTO.KakaoUserDTO;

public interface UserMapper {
    KakaoUserDTO findByKakaoId(String kakaoId);
    KakaoUserDTO findByUserId(long userId);
    void insertUser(KakaoUserDTO user);
    void reactivateUser(String kakaoId);
    void softDeleteUser(String kakaoId);
}
