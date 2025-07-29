package org.scoula.security.account.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.scoula.oauth.domain.DTO.KakaoUserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUser implements UserDetails {
    private final KakaoUserDTO user;

    @Override
    public String getUsername() {
        return user.getKakaoId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 임시 role 지정
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인에는 비밀번호가 없음
    }

    //소셜로그인의 경우 항상 true로 반환
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}