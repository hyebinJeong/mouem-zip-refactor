package org.scoula.oauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.oauth.domain.DTO.KakaoApiResponse;
import org.scoula.oauth.domain.DTO.KakaoTokenResponseDTO;
import org.scoula.oauth.domain.DTO.KakaoUserDTO;
import org.scoula.oauth.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthServiceImpl implements KakaoOAuthService {

    private final JwtProcessor jwtProcessor;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserMapper userMapper;

    @Override
    public String getAccessTokenFromKakao(String code) {
        try {
            URL url = new URL("https://kauth.kakao.com/oauth/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // POST 요청 본문 작성 가능하게 함

            // 요청 바디 작성
            String body = "grant_type=authorization_code"
                    + "&client_id=88a530611ac6fa5a18f5747f67b6a359"
                    + "&redirect_uri=http://localhost:8080/oauth/callback/kakao"
                    + "&code=" + code;

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            bw.write(body);
            bw.flush();
            bw.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            String result = sb.toString();
            // System.out.println("[카카오 토큰 응답] " + result);
            log.debug("[카카오 토큰 응답] {}", result);

            // JSON 파싱해서 access_token 추출
            KakaoTokenResponseDTO tokenResponse = objectMapper.readValue(result, KakaoTokenResponseDTO.class);
            return tokenResponse.getAccess_token();

        } catch (IOException e) {
            // e.printStackTrace();
            log.error("카카오 토큰 요청 실패", e);
            throw new RuntimeException("카카오 토큰 요청 실패", e);
        }
    }

    @Override
    public KakaoUserDTO loginWithKakao(String kakaoAccessToken) {
        // 카카오 사용자 정보 가져오기
        KakaoUserDTO kakaoUser = requestKakaoUserInfo(kakaoAccessToken);

        // 우리 DB에 사용자 있는지 확인 (없으면 저장)
        return userService.loginOrRegister(kakaoUser);
    }

    @Override
    public KakaoUserDTO requestKakaoUserInfo(String token) {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        try {
            // 액세스 토큰을 이용해 사용자 정보 요청
            HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            // 응답 수신
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responses = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responses.append(line);
            }
            String json = responses.toString();
            // System.out.println("[카카오 응답] " + json);
            log.debug("[카카오 응답] {}", json);

            // 요청 받은 json DTO 형식으로 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            KakaoApiResponse response = objectMapper.readValue(json, KakaoApiResponse.class);

            String kakaoId = response.getId();
            String name = response.getProperties().getNickname();
            String email = response.getKakao_account().getEmail();
            return new KakaoUserDTO(kakaoId, name, email, true);

        } catch (IOException e) {
            // e.printStackTrace();
            log.error("사용자 정보 요청 실패", e);
            throw new RuntimeException("사용자 정보 요청 실패", e);
        }
    }

    @Override
    public void softDeleteUser(String kakaoId) {
        userMapper.softDeleteUser(kakaoId);
    }

    @Value("${kakao.adminKey}")
    private String adminKey;

    @Override
    public void unlinkByAdminKey(Long kakaoId) {
        try {
            URL url = new URL("https://kapi.kakao.com/v1/user/unlink");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "KakaoAK " + adminKey);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            String body = "target_id_type=user_id&target_id=" + kakaoId;
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }

            int code = conn.getResponseCode();
            if (code != 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String errorBody = br.readLine();
                    log.error("Kakao unlink failed. status={}, body={}", code, errorBody);
                    throw new IllegalStateException("Kakao unlink failed: " + errorBody);
                }
            } else {
                log.debug("Kakao unlink success. kakaoId={}", kakaoId);
            }
        } catch (IOException e) {
            log.error("카카오 회원 탈퇴 요청 실패", e);
            throw new RuntimeException("카카오 회원 탈퇴 요청 실패", e);
        }
    }
}