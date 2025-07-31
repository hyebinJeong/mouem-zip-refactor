package org.scoula.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.oauth.domain.DTO.KakaoUserDTO;
import org.scoula.oauth.mapper.UserMapper;
import org.scoula.security.account.domain.CustomUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userIdStr) throws UsernameNotFoundException {
        Long userId;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("잘못된 user_id 형식: " + userIdStr);
        }

        KakaoUserDTO user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new UsernameNotFoundException("사용자 없음: " + userId);
        }

        return new CustomUser(user);
    }
}
