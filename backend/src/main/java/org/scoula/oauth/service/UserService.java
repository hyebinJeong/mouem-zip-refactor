package org.scoula.oauth.service;

import org.scoula.oauth.domain.DTO.KakaoUserDTO;
import org.scoula.oauth.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public KakaoUserDTO loginOrRegister(KakaoUserDTO kakaoUser) {
        KakaoUserDTO user = userMapper.findByKakaoId(kakaoUser.getKakaoId());

        if (user == null) {
            userMapper.insertUser(kakaoUser);
            return kakaoUser; // 새로 저장한 유저 반환
        }

        return user; // 기존 유저 반환
    }
}
