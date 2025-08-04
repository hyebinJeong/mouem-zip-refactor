package org.scoula.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProcessor {
    static private final long TOKEN_VALID_MILISECOND = 1000L * 60 * 60; // 60 분
    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        // secretKey가 주입된 이후에 Key 생성
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
// private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); -- 운영시 사용

    // JWT 생성
    public String generateToken(String subject, String kakaoId, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("kakaoId", kakaoId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + TOKEN_VALID_MILISECOND))
                .signWith(key)
                .compact();
    }

    // JWT Subject(username) 추출- 해석 불가인 경우 예외 발생
    // 예외 ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException,
    // IllegalArgumentException
    public String getUserId(String token) {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return subject;
    }

    public String getKakaoId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("kakaoId", String.class);
    }

    // JWT 검증(유효 기간 검증) - 해석 불가인 경우 예외 발생
    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return true;
    }
}
