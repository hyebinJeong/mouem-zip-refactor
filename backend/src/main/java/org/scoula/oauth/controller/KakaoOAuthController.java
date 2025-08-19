package org.scoula.oauth.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.oauth.domain.DTO.KakaoUserDTO;
import org.scoula.oauth.mapper.UserMapper;
import org.scoula.oauth.service.KakaoOAuthService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth/kakao")
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;


    // 카카오 로그인 요청 -> access_token -> jwt 변환
    @PostMapping("/login")
    public ResponseEntity<?> kakaoLogin(
            @RequestBody Map<String, String> body,
            HttpServletResponse response
    ) {
        String code = body.get("access_token"); // 카카오 인가 코드
        // 카카오 access_token 발급
        String kakaoAccessToken = kakaoOAuthService.getAccessTokenFromKakao(code);

        KakaoUserDTO user = kakaoOAuthService.loginWithKakao(kakaoAccessToken);

        // Access / Refresh 토큰 발급
        String accessToken = jwtProcessor.generateAccessToken(
                String.valueOf(user.getUserId()),
                user.getKakaoId(),
                user.getRole()
        );
        String refreshToken = jwtProcessor.generateRefreshToken(String.valueOf(user.getUserId()));

        // Refresh 토큰을 HttpOnly 쿠키로 설정
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // 개발환경은 false, 배포는 true
                .path("/")
                .sameSite("Lax") // 개발에서는 크로스도메인 허용
                .maxAge(60 * 60 * 24 * 14) // 브라우저에 얼마나 보관할지
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // Access 토큰만 응답 body로 반환
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || !jwtProcessor.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or missing refresh token"));
        }

        String userId = jwtProcessor.getUserId(refreshToken);

        KakaoUserDTO user = userMapper.findByUserId(Long.parseLong(userId));

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        // 새 Access 발급 (role, name 등 포함)
        String newAccess = jwtProcessor.generateAccessToken(
                String.valueOf(user.getUserId()),
                user.getKakaoId(),
                user.getRole()
        );

        // Refresh 회전
        String newRefresh = jwtProcessor.generateRefreshToken(userId);
        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefresh)
                .httpOnly(true)
                .secure(false) // 배포 시 true
                .sameSite("Lax") // 배포 상황시 설정
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(Map.of("accessToken", newAccess));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Refresh 쿠키 만료 처리
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0) // 로그 아웃시 토큰 즉시 만료
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/unlink")
    public ResponseEntity<Void> unlink(@RequestHeader("Authorization") String authorizationHeader) {
        String jwt = authorizationHeader.replace("Bearer ", "");
        String kakaoId = jwtProcessor.getKakaoId(jwt);


        kakaoOAuthService.unlinkByAdminKey(Long.parseLong(kakaoId));
        kakaoOAuthService.softDeleteUser(kakaoId); // state=false 등

        return ResponseEntity.ok().build();
    }
}