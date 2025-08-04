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
            return userMapper.findByKakaoId(kakaoUser.getKakaoId()); // 새로 저장한 유저 반환
        }else {
            if (!user.isState()) {
                // 소프트딜리트 상태 → 복구 처리
                userMapper.reactivateUser(kakaoUser.getKakaoId());
                return userMapper.findByKakaoId(kakaoUser.getKakaoId());
            }
            return user; // 기존 유저 반환
        }
    }
}
