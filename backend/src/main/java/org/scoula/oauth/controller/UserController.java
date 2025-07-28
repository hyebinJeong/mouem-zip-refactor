package org.scoula.oauth.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.oauth.domain.DTO.KakaoUserDTO;
import org.scoula.oauth.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<KakaoUserDTO> getCurrentUser(Authentication authentication) {
        String kakaoId = authentication.getName(); // JWT에서 추출된 subject (보통 kakaoId)

        KakaoUserDTO user = userMapper.findByKakaoId(kakaoId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(user);
    }
}